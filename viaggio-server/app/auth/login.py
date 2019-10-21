import os
from flask import jsonify, request
from google.oauth2 import id_token
from google.auth.transport import requests
from . import auth
from .. import db
from ..models import User
from ..forms.user import LoginForm
from ..errors import bad_request
import uuid


@auth.route('/login', methods=['POST'])
def login():
    form = LoginForm(request.form)
    if form.validate():
        user = User.query.filter_by(email=request.form['email'],
                                    passwordHash=request.form['passwordHash']).first()
        if user is None:
            return bad_request(401, 'There is no user matched with email or pwd.')

        if user.token:
            try:
                response = user.get_aws_token()
            except:
                return bad_request(402, 'Get AWS validation is failed.')

            return jsonify({
                'email': user.email,
                'name': user.name,
                'imageUrl': user.profileImageUrl,
                'token': user.token,
                'AWS_IdentityId': response['IdentityId'],
                'AWS_Token': response['Token'] }), 200
        else:
            user.token = str(uuid.uuid4())
            db.session.add(user)
            db.session.commit()
            try:
                response = user.get_aws_token()
            except:
                return bad_request(402, 'Get AWS validation is failed.')

            return jsonify({
                'email': user.email,
                'name': user.name,
                'token': user.token,
                'imageUrl': user.profileImageUrl,
                'AWS_IdentityId': response['IdentityId'],
                'AWS_Token': response['Token'] }), 200

    return bad_request(400, 'Login valdation is failed.')


@auth.route('/googlesignin', methods=['POST'])
def google_signin():
    if not request.json.get('id_token') or request.json.get('id_token') is None:
        return bad_request(400, 'Google id_token is not exist.')
    try:
        token = request.json.get('id_token')
        idinfo = id_token.verify_oauth2_token(token, requests.Request(), os.environ.get('CLIENT_ID'))

        if idinfo['iss'] not in ['accounts.google.com', 'https://accounts.google.com']:
            raise ValueError('Wrong issuer.')

    except ValueError:
        return bad_request(400, 'Google token is invalid.')
    else:
        user = User.query.filter_by(email=idinfo['email']).first()
        if user is not None:
            if user.googleId is None:
                return bad_request(400, 'Email already exist.')
            if user.token:
                try:
                    response = user.get_aws_token()
                except:
                    return bad_request(403, 'Get AWS validation is failed.')
                return jsonify({
                    'email': user.email,
                    'name': user.name,
                    'token': user.token,
                    'imageUrl': user.profileImageUrl,
                    'AWS_IdentityId': response['IdentityId'],
                    'AWS_Token': response['Token'],
                    'isGoogleId': True }), 200
            else:
                user.token = str(uuid.uuid4())
                db.session.add(user)
                db.session.commit()
                try:
                    response = user.get_aws_token()
                except:
                    return bad_request(403, 'Get AWS validation is failed.')

                return jsonify({
                    'email': user.email,
                    'name': user.name,
                    'token': user.token,
                    'imageUrl': user.profileImageUrl,
                    'AWS_IdentityId': response['IdentityId'],
                    'AWS_Token': response['Token'] }), 200
        else:
            user = User(googleId=idinfo['sub'],
                        name=idinfo['name'],
                        email=idinfo['email'])
            db.session.add(user)
            db.session.commit()
            try:
                response = user.get_aws_token()
            except:
                return bad_request(403, 'Get AWS validation is failed.')

            return jsonify({
                'email': user.email,
                'name': user.name,
                'token': user.token,
                'imageUrl': user.profileImageUrl,
                'AWS_IdentityId': response['IdentityId'],
                'AWS_Token': response['Token'],
                'isGoogleId': True }), 200

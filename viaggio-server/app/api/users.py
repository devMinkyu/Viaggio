from flask import jsonify, request
from . import api
from .. import db
from ..models import User
from ..forms.user import ChangePasswordForm, ChangeUserInfoForm
from ..errors import bad_request
import uuid


@api.route('/users/changepwd', methods=['POST'])
def change_password():
    form = ChangePasswordForm(request.form)
    if form.validate():
        # Todo: Exception When token is not exist.
        user = User.query.filter_by(token=request.headers['authorization'],
                                    passwordHash=request.form['oldPasswordHash']).first()
        if user is None:
            return bad_request(401, 'There is no user matched with token and pwd when change user pwd.')
        user.passwordHash = request.form['passwordHash']
        db.session.add(user)
        db.session.commit()
        return jsonify({ 'result': 'User password is changed.' }), 200
    
    if form.passwordHash.errors:
        return bad_request(402, 'When change pwd, validation error is occurred.')

    return bad_request(403, 'Change password validation is failed.')


@api.route('/users/changeinfo', methods=['POST'])
def change_name():
    form = ChangeUserInfoForm(request.form)
    if form.validate():
        # Todo: Exception When token is not exist.
        user = User.query.filter_by(token=request.headers['authorization']).first()
        if user is None:
            return bad_request(401, 'There is no user matched with token when change user name.')
        user.name = request.form['name']
        if request.form.get('profileImageName') is not None \
                and request.form.get('profileImageUrl') is not None:
            user.profileImageName = request.form['profileImageName']
            user.profileImageUrl = request.form['profileImageUrl']
        db.session.add(user)
        db.session.commit()
        return jsonify({ 'result': 'User info is changed.' }), 200
    
    return bad_request(400, 'When change user info, validation error is occurred.')


@api.route('/users/logout')
def logout():
    user = User.query.filter_by(token=request.headers['authorization']).first()
    if user is None:
        return jsonify({ 'result': 'Token is already null.' }), 200

    user.token = None
    db.session.add(user)
    db.session.commit()
    return jsonify({ 'result': 'User logout is success.' }), 200

from flask import jsonify, request
from . import api
from .. import db
from ..models import User
from ..forms.user import RegistrationForm, ChangePasswordForm, ChangeUserNameForm, LoginForm
import uuid


@api.route('/users', methods=['POST'])
def create_user():
    form = RegistrationForm(request.form)
    if form.validate():
        user = User(email=request.form['email'],
                    name=request.form['name'],
                    passwordHash=request.form['passwordHash'],
                    profileImageName=request.form.get('profileImageName', ''))
        db.session.add(user)
        db.session.commit()
        return jsonify({ 'email': user.email, 'name': user.name, 'token': user.token }), 200

    if form.passwordHash.errors:
        return jsonify({
            'message': 400,
            'detail': form.passwordHash.errors[0]
        }), 400

    if form.email.errors:
        return jsonify({
            'message': 401,
            'detail': form.email.errors[0]
        }), 400

    return jsonify({
        'message': 402,
        'detail': 'User vaildation is failed'
    }), 400


@api.route('/users/changepwd', methods=['POST'])
def change_password():
    form = ChangePasswordForm(request.form)
    if form.validate():
        user = User.query.filter_by(token=request.headers['token'], passwordHash=request.form['oldPasswordHash']).first()
        if user is None:
            return jsonify({
                'message': 401,
                'detail': 'There is no user matched with token and pwd when change user pwd.'
            }), 400
        user.passwordHash = request.form['passwordHash']
        db.session.add(user)
        db.session.commit()
        return jsonify({
            'message': 200,
            'detail': 'User password is changed.'
        }), 200
    
    if form.passwordHash.errors:
        return jsonify({
            'message': 402,
            'detail': 'When change pwd, validation error is occurred.'
        }), 400

    return jsonify({
        'message': 403,
        'detail': 'Change password validation is failed.'
    }), 400


@api.route('/users/changename', methods=['POST'])
def change_name():
    form = ChangeUserNameForm(request.form)
    if form.validate():
        user = User.query.filter_by(token=request.headers['token']).first()
        if user is None:
            return jsonify({
                'message': 401,
                'detail': 'There is no user matched with token when change user name.'
            }), 400
        user.name = request.form['name']
        db.session.add(user)
        db.session.commit()
        return jsonify({
            'message': 200,
            'detail': 'User name is changed.'
        }), 200
    
    return jsonify({
        'message': 400,
        'detail': 'When change user name, validation error is occurred.'
    }), 400


@api.route('/users/login', methods=['POST'])
def login():
    form = LoginForm(request.form)
    if form.validate():
        user = User.query.filter_by(email=request.form['email'], passwordHash=request.form['passwordHash']).first()
        if user is None:
            return jsonify({
                'message': 401,
                'detail': 'There is no user matched with pwd.'
            }), 400

        if user.token:
            return jsonify({
                'message': 200,
                'token': user.token
            }), 200
        else:
            user.token = str(uuid.uuid4())
            db.session.add(user)
            db.session.commit()
            return jsonify({
                'message': 200,
                'token': user.token
            }), 200

    return jsonify({
        'message': 400,
        'detail': 'Login validation is failed.'
    }), 400


@api.route('/users/logout')
def logout():
    user = User.query.filter_by(token=request.headers['token']).first()
    if user is None:
        return jsonify({
            'message': 200,
            'detail': 'Token is already null.'
        }), 200

    user.token = ''
    db.session.add(user)
    db.session.commit()
    return jsonify({
        'message': 200,
        'detail': 'User logout is success.'
    }), 200

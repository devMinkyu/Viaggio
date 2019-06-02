from flask import jsonify, request
from . import api
from .. import db
from ..models import User
from ..forms.user import ChangePasswordForm, ChangeUserInfoForm
from ..errors import bad_request, unauthorized


@api.before_request
def before_request():
    if request.method == 'OPTIONS':
        return ('', 204)
    if request.headers.get('auth') is None:
        return unauthorized('Token is not exist.')
    if User.query.filter_by(token=request.headers['auth']).first() is None:
        return unauthorized('There is no user matched with token.')
    else:
        request.user = User.query.filter_by(token=request.headers['auth']).first()


@api.route('/users/changepwd', methods=['POST'])
def change_password():
    form = ChangePasswordForm(request.form)
    if form.validate():
        if request.user is None:
            return bad_request(401, 'There is no user matched with token and pwd when change user pwd.')
        if request.user.passwordHash != request.form['oldPasswordHash']:
            return bad_request(402, 'Password is not correct.')
        request.user.passwordHash = request.form['passwordHash']
        db.session.add(request.user)
        db.session.commit()
        return jsonify({ 'result': 'User password is changed.' }), 200
    
    if form.passwordHash.errors:
        return bad_request(403, 'When change pwd, validation error is occurred.')

    return bad_request(404, 'Change password validation is failed.')


@api.route('/users/changeinfo', methods=['POST'])
def change_name():
    form = ChangeUserInfoForm(request.form)
    if form.validate():
        if request.user is None:
            return bad_request(401, 'There is no user matched with token when change user name.')
        request.user.name = request.form['name']
        if request.form.get('profileImageUrl') is not None:
            request.user.profileImageUrl = request.form['profileImageUrl']
        db.session.add(request.user)
        db.session.commit()
        return jsonify({ 'result': 'User info is changed.' }), 200
    
    return bad_request(400, 'When change user info, validation error is occurred.')


@api.route('/users/logout')
def logout():
    if request.user is None:
        return jsonify({ 'result': 'Token is already null.' }), 200

    request.user.token = None
    db.session.add(request.user)
    db.session.commit()
    return jsonify({ 'result': 'User logout is success.' }), 200

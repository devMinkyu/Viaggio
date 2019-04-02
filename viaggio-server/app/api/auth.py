from flask import jsonify, request
from . import api
from .. import db
from ..models import User
from ..forms.user import RegistrationForm


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

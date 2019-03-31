from flask import jsonify, request
from . import api
from .. import db
from ..models import User
from ..forms.user import UserForm


@api.route('/users', methods=['POST'])
def create_user():
    form = UserForm(request.form)
    if form.validate():
        user = User(email=request.form['email'],
                    name=request.form['name'],
                    passwordHash=request.form['passwordHash'],
                    profileImageName=request.form.get('profileImageName', ''))
        db.session.add(user)
        db.session.commit()
        return jsonify({ 'email': user.email, 'name': user.name, 'token': user.token })       
    if form.email.errors:
        return jsonify({ 'message': form.email.errors[0] })
    return jsonify({ 'message': 'User vaildation is failed' })

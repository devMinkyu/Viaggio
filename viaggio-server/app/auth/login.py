from flask import jsonify, request
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
            return jsonify({ 'token': user.token }), 200
        else:
            user.token = str(uuid.uuid4())
            db.session.add(user)
            db.session.commit()
            return jsonify({ 'token': user.token }), 200

    return bad_request(400, 'Login valdation is failed.')

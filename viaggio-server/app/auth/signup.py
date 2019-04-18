from flask import jsonify, request
from . import auth
from .. import db
from ..models import User
from ..forms.user import RegistrationForm
from ..errors import bad_request


@auth.route('/signup', methods=['POST'])
def create_user():
    form = RegistrationForm(request.form)
    if form.validate():
        user = User(email=request.form['email'],
                    name=request.form['name'],
                    passwordHash=request.form['passwordHash'])
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
            'AWS_IdentityId': response['IdentityId'],
            'AWS_Token': response['Token'] }), 200

    if form.passwordHash.errors:
        return bad_request(401, form.passwordHash.errors[0])

    if form.email.errors:
        return bad_request(402, form.email.errors[0])

    return bad_request(400, 'User vaildation is failed.')

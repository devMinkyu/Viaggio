from flask import jsonify, request
from . import api
from .. import db
from ..models import User
from ..errors import bad_request


@api.route('/my/aws')
def aws():
    user = User.query.filter_by(token=request.headers.get('auth')).first()
    if user is None:
        return bad_request(401, 'There is no user matched with token.')

    try:
        response = user.get_aws_token()
    except:
        return bad_request(402, 'Get AWS validation is failed.')
    else:
        return jsonify({
            'AWS_IdentityId': response['IdentityId'],
            'AWS_Token': response['Token'] }), 200

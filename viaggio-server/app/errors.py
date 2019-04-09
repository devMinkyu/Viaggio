from flask import jsonify


def bad_request(message, detail):
    return jsonify({ 'message': message, 'detail': detail }), 400


def unauthorized(message, detail):
    response = jsonify({ 'error': 'unauthorized', 'detail': detail })
    response.status_code = 401
    return response


def forbidden(message, detail):
    response = jsonify({ 'error': 'forbidden', 'detail': detail })
    response.status_code = 403
    return response

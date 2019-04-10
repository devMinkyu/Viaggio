from flask import jsonify


def bad_request(message, detail):
    return jsonify({ 'message': message, 'detail': detail }), 400


def unauthorized(detail):
    return jsonify({ 'message': 'unauthorized', 'detail': detail }), 401


def forbidden(detail):
    return jsonify({ 'message': 'forbidden', 'detail': detail }), 403

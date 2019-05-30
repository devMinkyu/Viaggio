from flask import jsonify, request, json
from . import commons
from .. import db
from ..errors import bad_request
import os


@commons.route('/countries')
def get_contries():
    data = json.load(open('app/static/country.json'))

    return jsonify(data), 200


@commons.route('/domestics')
def get_domestics():
    data = json.load(open('app/static/domestic.json'))

    return jsonify({ 'domestics': data }), 200


@commons.route('/themes')
def get_themes():
    data = json.load(open('app/static/theme.json'))

    return jsonify({ 'themes': data }), 200

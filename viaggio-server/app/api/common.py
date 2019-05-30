from flask import jsonify, request, json
from . import api
from .. import db
from ..models import Travel, TravelCard
from ..errors import bad_request
import os


@api.route('/common/countries')
def get_contries():
    data = json.load(open('app/static/country.json'))

    return jsonify(data), 200


@api.route('/common/domestics')
def get_domestics():
    data = json.load(open('app/static/domestic.json'))

    return jsonify({ 'domestics': data }), 200


@api.route('/common/themes')
def get_themes():
    data = json.load(open('app/static/theme.json'))

    return jsonify({ 'themes': data }), 200

from flask import jsonify, request
from . import api
from .. import db
from ..models import Travel, TravelCard, AnalysisTheme, AnalysisContinent, AnalysisCountry, AnalysisCity, AnalysisSubCity
from ..errors import bad_request


@api.route('/my/travels', methods=['POST'])
def create_travel():
    if request.json.get('localId') is None:
        return bad_request(400, 'LocalId is required.')
    if Travel.query.filter_by(localId=request.json.get('localId')).first():
        return bad_request(400, 'LocalId already exist.')
    if request.json.get('startDate') is None:
        return bad_request(400, 'startDate is required.')
    if request.json.get('travelKind') is None:
        return bad_request(400, 'travelKind is required.')

    travel = Travel(userId=request.user.id,
                    localId=request.json.get('localId'),
                    startDate=request.json.get('startDate'),
                    endDate=request.json.get('endDate'),
                    travelKind=request.json.get('travelKind'),
                    area=request.json.get('area'),
                    title=request.json.get('title'),
                    theme=request.json.get('theme'),
                    imageName=request.json.get('imageName'),
                    imageUrl=request.json.get('imageUrl'))

    try:
        db.session.add(travel)
        db.session.commit()
        return jsonify({ 'id': travel.id }), 200
    except:
        db.session.rollback()
        return bad_request(400, 'Create travel is failed.')


@api.route('/my/travels')
def get_travels():
    travels = Travel.query.filter_by(userId=request.user.id)
    return jsonify({
        'travels': [travel.to_json() for travel in travels]
    }), 200


@api.route('/my/travels/<int:id>')
def get_specific_travel(id):
    travel = Travel.query.filter_by(id=id).first_or_404()
    return jsonify({
        'travel': travel.as_dict()
    }), 200


@api.route('/my/travels/<int:id>', methods=['PUT'])
def update_travel(id):
    travel = Travel.query.get_or_404(id)
    if request.json.get('title') is not None:
        travel.title = request.json.get('title')
    if request.json.get('area') is not None:
        tempArea = request.json.get('area')
        travel.area = tempArea
    if request.json.get('endDate') is not None:
        travel.endDate = request.json.get('endDate')
    if request.json.get('theme') is not None:
        tempTheme = list(request.json.get('theme'))
        travel.theme = tempTheme
    if request.json.get('imageName'):
        travel.imageName = request.json.get('imageName')
    if request.json.get('imageUrl'):
        travel.imageUrl = request.json.get('imageUrl')
    if request.json.get('share'):
        travel.share = True
    else:
        travel.share = False
    
    try:
        db.session.commit()
        return jsonify({ 'result': 'Update travel is success.' }), 200
    except:
        return jsonify({ 'result': 'Update travel is failed.' }), 500


@api.route('/my/travels/<int:id>', methods=['DELETE'])
def delete_travel(id):
    travel = Travel.query.get_or_404(id)
    travel.isDelete = True
    db.session.add(travel)
    db.session.commit()
    return jsonify({ 'result': 'Travel is archived.' })

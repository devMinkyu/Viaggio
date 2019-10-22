from flask import jsonify, request
from . import api
from .. import db
from ..models import Travel, TravelCard
from ..errors import bad_request


@api.route('/my/travelcards/<int:travelId>', methods=['POST'])
def create_travelCard(travelId):
    if request.json.get('localId') is None:
        return bad_request(400, 'localId is required.')
    if TravelCard.query.filter_by(localId=request.json.get('localId')).first():
        return bad_request(400, 'LocalId already exist.')
    if request.json.get('travelLocalId') is None:
        return bad_request(400, 'travelLocalId is required.')

    travelCard = TravelCard(travelId=travelId,
                            localId=request.json.get('localId'),
                            travelLocalId=request.json.get('travelLocalId'),
                            travelOfDay=request.json.get('travelOfDay'),
                            country=request.json.get('country'),
                            theme=request.json.get('theme'),
                            content=request.json.get('content'),
                            imageName=request.json.get('imageNames'),
                            imageUrl=request.json.get('imageUrl'),
                            date=request.json.get('date'),
                            time=request.json.get('time'))

    try:
        db.session.add(travelCard)
        db.session.commit()
        return jsonify({ 'id': travelCard.id }), 200
    except:
        return jsonify({ 'result': 'Create TravelCard is failed.' }), 500


@api.route('/my/travelcards/<int:travelId>')
def get_travelCards(travelId):
    travelCards = TravelCard.query.filter_by(travelId=travelId)
    return jsonify({
        'travelCards': [travelCard.to_json() for travelCard in travelCards]
    }), 200


@api.route('/my/travelcards/<int:travelCardId>')
def get_travelCard(travelCardId):
    travelCard = TravelCard.query.filter_by(id=travelCardId).first_or_404()
    return jsonify({
        'travelCard': travelCard.to_json()
    }), 200


@api.route('/my/travelcards')
def get_allTravelCard():
    travels = Travel.query.filter_by(userId=request.user.id, isDelete=False)
    travels = [travel.to_json() for travel in travels]
    travelCards = []
    for travel in travels:
        tempTravelCards = TravelCard.query.filter_by(travelId=travel['serverId'], isDelete=False)
        tempTravelCards = [travelCard.to_json() for travelCard in tempTravelCards]
        for travelCard in tempTravelCards:
            travelCards.append(travelCard)
        
    return jsonify({ 'travelCards': travelCards }), 200


@api.route('/my/travelcards/<int:travelCardId>', methods=['PUT'])
def update_travelCard(travelCardId):
    travelCard = TravelCard.query.filter_by(id=travelCardId).first_or_404()
    if request.json.get('content') is not None:
        travelCard.content = request.json.get('content')
    if request.json.get('country') is not None:
        travelCard.country = request.json.get('country')
    if request.json.get('theme') is not None:
        travelCard.theme = request.json.get('theme')
    if request.json.get('imageNames') is not None:
        travelCard.imageName = request.json.get('imageNames')
    if request.json.get('imageUrl') is not None:
        travelCard.imageUrl = request.json.get('imageUrl')
    if request.json.get('date') is not None:
        travelCard.date = request.json.get('date')
    if request.json.get('time') is not None:
        travelCard.time = request.json.get('time')
    db.session.commit()
    return jsonify({ 'result': 'Travel card is updated.' })


@api.route('/my/travelcards/<int:travelCardId>', methods=['DELETE'])
def delete_travelCard(travelCardId):
    travelCard = TravelCard.query.filter_by(id=travelCardId).first_or_404()
    travelCard.isDelete = True
    db.session.commit()
    return jsonify({ 'result': 'Travel card is archived.' })

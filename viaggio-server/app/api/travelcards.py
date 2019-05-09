from flask import jsonify, request
from . import api
from .. import db
from ..models import Travel, TravelCard
from ..forms.travel import CreateTravelCardForm
from ..errors import bad_request


@api.route('/my/travelcards/<int:travelId>', methods=['POST'])
def create_travelCard(travelId):
    form = CreateTravelCardForm(request.form)
    if form.validate():
        travelCard = TravelCard(travelId=travelId,
                                localId=request.form.get('localId'),
                                travelLocalId=request.form.get('travelLocalId'),
                                travelOfDay=request.form.get('travelOfDay'),
                                country=request.form.get('country'),
                                theme=request.form.get('theme'),
                                content=request.form.get('content'),
                                imageName=request.form.get('imageName'),
                                imageUrl=request.form.get('imageUrl'),
                                date=request.form.get('date'))
        db.session.add(travelCard)
        db.session.commit()
        return jsonify({ 'travelCard': travelCard.as_dict() }), 200

    if form.localId.errors:
        return bad_request(401, form.localId.errors[0])


@api.route('/my/travelcards/<int:travelId>')
def get_travelCards(travelId):
    travelCards = TravelCard.query.filter_by(travelId=travelId)
    return jsonify({
        'travelCards': [travelCard.to_json() for travelCard in travelCards]
    }), 200


@api.route('/my/travelcard/<int:travelCardId>')
def get_travelCard(travelCardId):
    travelCard = TravelCard.query.filter_by(id=travelCardId).first_or_404()
    return jsonify({
        'travelCard': travelCard.as_dict()
    }), 200


@api.route('/my/travelcards/<int:travelCardId>', methods=['PUT'])
def update_travelCard(travelCardId):
    travelCard = TravelCard.query.filter_by(id=travelCardId).first_or_404()
    if request.form.get('travelOfDay') is not None:
        travelCard.travelOfDay = request.form.get('travelOfDay')
    if request.form.get('country') is not None:
        travelCard.country = request.form.get('country')
    if request.form.get('theme') is not None:
        tempTheme = list(request.form.get('theme'))
        travelCard.theme = tempTheme
    if request.form.get('content') is not None:
        travelCard.content = request.form.get('content')
    if request.form.get('imageName') is not None:
        tempImageNeme = list(request.form.get('imageName'))
        travelCard.imageName = tempImageNeme
    if request.form.get('imageUrl') is not None:
        tempImageUrl = list(request.form.get('imageUrl'))
        travelCard.imageUrl = tempImageUrl
    if request.form.get('date') is not None:
        travelCard.date = request.form.get('date')
    if request.form.get('isDelete') is not None:
        travelCard.isDelete = request.form.get('isDelete')
    db.session.commit()
    return jsonify({ 'travelCard': travelCard.as_dict() })


@api.route('/my/travelcards/<int:travelCardId>', methods=['DELETE'])
def delete_travelCard(travelCardId):
    travelCard = TravelCard.query.filter_by(id=travelCardId).first_or_404()
    travelCard.isDelete = True
    db.session.commit()
    return jsonify({ 'result': 'Travel card is archived.' })

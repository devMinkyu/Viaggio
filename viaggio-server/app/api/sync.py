from flask import jsonify, request
from . import api
from .. import db
from ..models import Travel, TravelCard
from ..errors import bad_request
from datetime import datetime


@api.route('/sync/count')
def get_count():
    travels = Travel.query.filter_by(userId=request.user.id)
    travels = [travel.to_json() for travel in travels]
    travelCount = Travel.query.filter_by(userId=request.user.id, isDelete=False).count()
    travelCardCount = 0

    for travel in travels:
        travelCardCount += TravelCard.query.filter_by(id=travel['serverId'], isDelete=False).count()

    return jsonify({
        'travelCount': travelCount,
        'travelCardCount': travelCardCount
    }), 200


@api.route('/sync/travels', methods=['POST'])
def create_travels():
    travels = request.json['travels']
    tempTravelCards = []
    for item in travels:
        item = Travel(userId=request.user.id,
                        localId=item.get('localId'),
                        startDate=item.get('startDate'),
                        endDate=item.get('endDate'),
                        travelKind=item.get('travelKind'),
                        area=item.get('area'),
                        title=item.get('title'),
                        theme=item.get('theme'),
                        imageName=item.get('imageName'),
                        imageUrl=item.get('imageUrl'))
        db.session.add(item)
        tempTravelCards.append(item)
    try:
        db.session.commit()
    except:
        db.session.rollback()
    createdTravels = [travelCard.sync_create_json() for travelCard in tempTravelCards]
    return jsonify({ 'travels': createdTravels })


# @api.route('/sync/travels', methods=['PUT'])
# def update_travels():
#     reqTravels = request.json['travels']
#     travels = Travel.query.filter_by(userId=request.user.id)
#     travels = [travel.to_json() for travel in travels]
#     for reqTravel in reqTravels:
        # list(filter(lambda x: x > 0, li))

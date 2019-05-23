from flask import jsonify, request
from . import api
from .. import db
from ..models import Travel, TravelCard, AnalysisTheme, AnalysisContinent, AnalysisCountry, AnalysisCity, AnalysisSubCity
from ..forms.travel import CreateTravelForm
from ..errors import bad_request
from datetime import datetime


@api.route('/my/travels', methods=['POST'])
def create_travel():
    form = CreateTravelForm(request.form)
    if form.validate():
        travel = Travel(userId=request.user.id,
                        localId=request.form.get('localId'),
                        startDate=datetime.strptime(request.form.get('startDate'), "%Y-%m-%d %H:%M:%S") if request.form.get('startDate') else request.form.get('startDate'),
                        endDate=datetime.strptime(request.form.get('endDate'), "%Y-%m-%d %H:%M:%S") if request.form.get('endDate') else request.form.get('endDate'),
                        travelKind=request.form.get('travelKind'),
                        area=request.form.get('area'),
                        title=request.form.get('title'),
                        theme=request.form.get('theme'),
                        imageName=request.form.get('imageName'),
                        imageUrl=request.form.get('imageUrl'))
        db.session.add(travel)
        db.session.commit()
        return jsonify({ 'id': travel.id }), 200

    if form.localId.errors:
        return bad_request(401, form.localId.errors[0])

    if form.startDate.errors:
        return bad_request(402, 'StartDate validation error.')

    if form.travelKind.errors:
        return bad_request(403, form.travelKind.errors[0])
    
    return bad_request(400, 'CreateTravelForm validation error.')


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
    if request.form.get('title') is not None:
        travel.title = request.form.get('title')
    if request.form.get('area') is not None:
        tempArea = request.form.get('area')
        travel.area = tempArea
    if request.form.get('endDate') is not None:
        travel.endDate = datetime.strptime(request.form.get('endDate'), "%Y-%m-%d %H:%M:%S")
    if request.form.get('theme') is not None:
        tempTheme = list(request.form.get('theme'))
        travel.theme = tempTheme
    if request.form.get('imageName'):
        travel.imageName = request.form.get('imageName')
    if request.form.get('imageUrl'):
        travel.imageUrl = request.form.get('imageUrl')
    if request.form.get('share'):
        travel.share = True
    else:
        travel.share = False
    db.session.commit()
    return jsonify({ 'travel': travel.as_dict() })


@api.route('/my/travels/<int:id>', methods=['DELETE'])
def delete_travel(id):
    travel = Travel.query.get_or_404(id)
    travel.isDelete = True
    db.session.add(travel)
    db.session.commit()
    return jsonify({ 'result': 'Travel is archived.' })

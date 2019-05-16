import os
from . import db
from datetime import datetime
import uuid
import boto3


class User(db.Model):
    __tablename__ = 'users'
    __table_args__ = {'mysql_collate': 'utf8_general_ci'}
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String(64), unique=True, index=True)
    name = db.Column(db.String(64))
    passwordHash = db.Column(db.String(128))
    profileImageName = db.Column(db.String(64))
    profileImageUrl = db.Column(db.String(512))
    token = db.Column(db.String(128), unique=True, default=lambda: str(uuid.uuid4()))
    createdDate = db.Column(db.DateTime, default=datetime.utcnow)
    travel = db.relationship('Travel', backref='user', lazy='dynamic')

    def __init__(self, **kwargs):
        super(User, self).__init__(**kwargs)

    def __repr__(self):
        return '<User %r>' % self.email, self.name, self.passwordHash, self.profileImageName, self.token, self.createdDate

    def as_dict(self):
        return {x.name: getattr(self, x.name) for x in self.__table__.columns}

    def get_aws_token(self):
        client = boto3.client('cognito-identity', region_name=os.environ.get('REGION'))
        response = client.get_open_id_token_for_developer_identity(
                        IdentityPoolId = os.environ.get('IdentityPoolId'),
                        Logins = {
                            os.environ.get('Logins'): str(self.id)
                        },
                        TokenDuration=86400
                    )
        return response


class Travel(db.Model):
    __tablename__ = 'travels'
    __table_args__ = {'mysql_collate': 'utf8_general_ci'}
    id = db.Column(db.Integer, primary_key=True)
    localId = db.Column(db.Integer, nullable=False, unique=True)
    userId = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)
    startDate = db.Column(db.DateTime)
    endDate = db.Column(db.DateTime)
    travelKind = db.Column(db.Integer)
    area = db.Column(db.PickleType)
    title = db.Column(db.String(64))
    theme = db.Column(db.PickleType)
    imageName = db.Column(db.String(32))
    imageUrl = db.Column(db.String(128))
    share = db.Column(db.Boolean, default=False)
    isDelete = db.Column(db.Boolean, default=False)
    travlecard = db.relationship('TravelCard', backref='travel', lazy='dynamic')

    def __init__(self, **kwargs):
        super(Travel, self).__init__(**kwargs)

    def __repr__(self):
        return '<Travel %r>' % self.startDate, self.endDate, self.title, self.theme,\
            self.imageName, self.imageUrl, self.share, self.isDelete

    def as_dict(self):
        return {x.name: getattr(self, x.name) for x in self.__table__.columns}

    def to_json(self):
        json_travel = {
            'serverId': self.id,
            'localId': self.localId,
            'startDate': self.startDate,
            'endDate': self.endDate,
            'travelKind': self.travelKind,
            'area': self.area,
            'title': self.title,
            'theme': self.theme,
            'imageName': self.imageName,
            'imageUrl': self.imageUrl,
            'share': self.share,
            'isDelete': self.isDelete
        }
        return json_travel


class TravelCard(db.Model):
    __tablename__ = 'travelcards'
    __table_args__ = {'mysql_collate': 'utf8_general_ci'}
    id = db.Column(db.Integer, primary_key=True)
    travelId = db.Column(db.Integer, db.ForeignKey('travels.id'), nullable=False)
    localId = db.Column(db.Integer, nullable=False, unique=True)
    travelLocalId = db.Column(db.Integer, nullable=False)
    travelOfDay = db.Column(db.Integer, default=1)
    country = db.Column(db.String(32))
    theme = db.Column(db.PickleType)
    content = db.Column(db.String(1024)) # TODO: Change to textarea.
    imageName = db.Column(db.PickleType)
    imageUrl = db.Column(db.PickleType)
    date = db.Column(db.DateTime)
    isDelete = db.Column(db.Boolean, default=False)

    def __init__(self, **kwargs):
        super(TravelCard, self).__init__(**kwargs)

    def __repr__(self):
        return '<TravelCard %r' % self.travelId, self.travelOfDay, self.country, \
            self.content, self.imageName, self.imageUrl, self.date

    def as_dict(self):
        return {x.name: getattr(self, x.name) for x in self.__table__.columns}

    def to_json(self):
        json_travelCard = {
            'serverId': self.id,
            'travelId': self.travelId,
            'localId': self.localId,
            'travelLocalId': self.travelLocalId,
            'travelOfDay': self.travelOfDay,
            'country': self.country,
            'theme': self.theme,
            'content': self.content,
            'imageName': self.imageName,
            'imageUrl': self.imageUrl,
            'date': self.date
        }
        return json_travelCard


class AnalysisTheme(db.Model):
    __tablename__ = 'analysisthemes'
    __table_args__ = {'mysql_collate': 'utf8_general_ci'}
    id = db.Column(db.Integer, primary_key=True)
    theme = db.Column(db.String(16))
    count = db.Column(db.Integer, default=0)

    def __init__(self, **kwargs):
        super(AnalysisTheme, self).__init__(**kwargs)

    def to_json(self):
        json_analysisTheme = {
            'id': self.id,
            'theme': self.theme,
            'count': self.count
        }
        return json_analysisTheme


class AnalysisContinent(db.Model):
    __tablename__ = 'analysiscontinents'
    __table_args__ = {'mysql_collate': 'utf8_general_ci'}
    id = db.Column(db.Integer, primary_key=True)
    continent = db.Column(db.String(32))
    count = db.Column(db.Integer, default=0)
    analysisCountry = db.relationship('AnalysisCountry', backref='analysiscontinent', lazy='dynamic')

    def __init__(self, **kwargs):
        super(AnalysisContinent, self).__init__(**kwargs)

    def to_json(self):
        json_analysisContinent = {
            'id': self.id,
            'continent': self.continent,
            'count': self.count
        }
        return json_analysisContinent


class AnalysisCountry(db.Model):
    __tablename__ = 'analysiscountries'
    __table_args__ = {'mysql_collate': 'utf8_general_ci'}
    id = db.Column(db.Integer, primary_key=True)
    analysisContinentId = db.Column(db.Integer, db.ForeignKey('analysiscontinents.id'), nullable=False)
    country = db.Column(db.String(32))
    count = db.Column(db.Integer, default=0)

    def __init__(self, **kwargs):
        super(AnalysisCountry, self).__init__(**kwargs)

    def to_json(self):
        json_analysisCountry = {
            'id': self.id,
            'analysisContinentId': self.analysisContinentId,
            'country': self.country,
            'count': self.count
        }
        return json_analysisCountry


class AnalysisCity(db.Model):
    __tablename__ = 'analysiscities'
    __table_args__ = {'mysql_collate': 'utf8_general_ci'}
    id = db.Column(db.Integer, primary_key=True)
    city = db.Column(db.String(16))
    count = db.Column(db.Integer, default=0)
    analysisCity = db.relationship('AnalysisSubCity', backref='analysisCity', lazy='dynamic')

    def __init__(self, **kwargs):
        super(AnalysisCity, self).__init__(**kwargs)

    def to_json(self):
        json_analysisCity = {
            'id': self.id,
            'city': self.city,
            'count': self.count
        }
        return json_analysisCity


class AnalysisSubCity(db.Model):
    __tablename__ = 'analysissubcities'
    __table_args__ = {'mysql_collate': 'utf8_general_ci'}
    id = db.Column(db.Integer, primary_key=True)
    analysisCityId = db.Column(db.Integer, db.ForeignKey('analysiscities.id'), nullable=False)
    subCity = db.Column(db.String(16))
    count = db.Column(db.Integer, default=0)

    def __init__(self, **kwargs):
        super(AnalysisSubCity, self).__init__(**kwargs)

    def to_json(self):
        json_analysisSubCity = {
            'id': self.id,
            'analysisCityId': self.analysisCityId,
            'subCity': self.subCity,
            'count': self.count
        }
        return json_analysisSubCity

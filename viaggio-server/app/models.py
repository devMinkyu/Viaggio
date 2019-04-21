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
    token = db.Column(db.String(128), unique=True, default=str(uuid.uuid4()))
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
    entireCountry = db.Column(db.PickleType)
    title = db.Column(db.String(64))
    theme = db.Column(db.PickleType)
    backgroundImageName = db.Column(db.String(32))
    backgroundImageUrl = db.Column(db.String(128))
    share = db.Column(db.Boolean, default=False)
    isDelete = db.Column(db.Boolean, default=False)
    travlecard = db.relationship('TravelCard', backref='travel', lazy='dynamic')

    def __init__(self, **kwargs):
        super(Travel, self).__init__(**kwargs)

    def __repr__(self):
        return '<Travel %r>' % self.startDate, self.endDate, self.title, self.theme,\
            self.backgroundImageName, self.backgroundImageUrl, self.share, self.isDelete

    def as_dict(self):
        return {x.name: getattr(self, x.name) for x in self.__table__.columns}

    def to_json(self):
        json_travel = {
            'id': self.id,
            'localId': self.localId,
            'userId': self.userId,
            'startDate': self.startDate,
            'endDate': self.endDate,
            'travelKind': self.travelKind,
            'entireCountry': self.entireCountry,
            'title': self.title,
            'theme': self.theme,
            'backgroundImageName': self.backgroundImageName,
            'backgroundImageUrl': self.backgroundImageUrl,
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
    title = db.Column(db.String(32))
    content = db.Column(db.String(1024))
    imageName = db.Column(db.String(32))
    imageUrl = db.Column(db.String(128))
    date = db.Column(db.DateTime)
    isDelete = db.Column(db.Boolean, default=False)

    def __init__(self, **kwargs):
        super(TravelCard, self).__init__(**kwargs)

    def __repr__(self):
        return '<TravelCard %r' % self.travelId, self.travelOfDay, self.country, self.title, \
            self.content, self.imageName, self.imageUrl, self.date

    def as_dict(self):
        return {x.name: getattr(self, x.name) for x in self.__table__.columns}

    def to_json(self):
        json_travelCard = {
            'id': self.id,
            'travelId': self.travelId,
            'travelOfDay': self.travelOfDay,
            'country': self.country,
            'title': self.title,
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


class AnalysisContinent(db.Model):
    __tablename__ = 'analysiscontinents'
    __table_args__ = {'mysql_collate': 'utf8_general_ci'}
    id = db.Column(db.Integer, primary_key=True)
    continent = db.Column(db.String(32))
    count = db.Column(db.Integer, default=0)
    analysisCountry = db.relationship('AnalysisCountry', backref='analysiscontinent', lazy='dynamic')

    def __init__(self, **kwargs):
        super(AnalysisContinent, self).__init__(**kwargs)


class AnalysisCountry(db.Model):
    __tablename__ = 'analysiscountries'
    __table_args__ = {'mysql_collate': 'utf8_general_ci'}
    id = db.Column(db.Integer, primary_key=True)
    analysisContinentId = db.Column(db.Integer, db.ForeignKey('analysiscontinents.id'), nullable=False)
    country = db.Column(db.String(32))
    count = db.Column(db.Integer, default=0)

    def __init__(self, **kwargs):
        super(AnalysisCountry, self).__init__(**kwargs)


class AnalysisCity(db.Model):
    __tablename__ = 'analysiscities'
    __table_args__ = {'mysql_collate': 'utf8_general_ci'}
    id = db.Column(db.Integer, primary_key=True)
    city = db.Column(db.String(16))
    count = db.Column(db.Integer, default=0)
    analysisCity = db.relationship('AnalysisSubCity', backref='analysisCity', lazy='dynamic')

    def __init__(self, **kwargs):
        super(AnalysisCity, self).__init__(**kwargs)


class AnalysisSubCity(db.Model):
    __tablename__ = 'analysissubcities'
    __table_args__ = {'mysql_collate': 'utf8_general_ci'}
    id = db.Column(db.Integer, primary_key=True)
    analysisCityId = db.Column(db.Integer, db.ForeignKey('analysiscities.id'), nullable=False)
    subCity = db.Column(db.String(16))
    count = db.Column(db.Integer, default=0)

    def __init__(self, **kwargs):
        super(AnalysisSubCity, self).__init__(**kwargs)

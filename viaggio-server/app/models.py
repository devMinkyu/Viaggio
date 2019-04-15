from . import db
from datetime import datetime
import uuid


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


class Travel(db.Model):
    __tablename__ = 'travels'
    __table_args__ = {'mysql_collate': 'utf8_general_ci'}
    id = db.Column(db.Integer, primary_key=True)
    userId = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)
    startDate = db.Column(db.DateTime)
    endDate = db.Column(db.DateTime)
    travelType = db.Column(db.String(16))
    entireCountry = db.Column(db.PickleType)
    title = db.Column(db.String(64))
    thema = db.Column(db.PickleType)
    backgroundImageName = db.Column(db.String(32))
    backgroundImageUrl = db.Column(db.String(128))
    share = db.Column(db.Boolean, default=False)
    isDelete = db.Column(db.Boolean, default=False)
    travlecard = db.relationship('TravelCard', backref='travel', lazy='dynamic')

    def __init__(self, **kwargs):
        super(Travel, self).__init__(**kwargs)

    def __repr__(self):
        return '<Travel %r>' % self.startDate, self.endDate, self.title, self.thema,\
            self.backgroundImageName, self.backgroundImageUrl, self.share, self.isDelete

    def as_dict(self):
        return {x.name: getattr(self, x.name) for x in self.__table__.columns}

    def to_json(self):
        json_travel = {
            'id': self.id,
            'userId': self.userId,
            'startDate': self.startDate,
            'endDate': self.endDate,
            'entireCountry': self.entireCountry,
            'title': self.title,
            'thema': self.thema,
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
    travelOfDay = db.Column(db.Integer, default=1)
    country = db.Column(db.String(32))
    title = db.Column(db.String(32))
    content = db.Column(db.String(1024))
    imageName = db.Column(db.String(32))
    imageUrl = db.Column(db.String(128))
    date = db.Column(db.DateTime)

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

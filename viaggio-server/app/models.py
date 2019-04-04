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

    def __init__(self, **kwargs):
        super(User, self).__init__(**kwargs)

    def __repr__(self):
        return '<User %r>' % self.email, self.name, self.passwordHash, self.profileImageName, self.token, self.createdDate

    def as_dict(self):
        return {x.name: getattr(self, x.name) for x in self.__table__.columns}

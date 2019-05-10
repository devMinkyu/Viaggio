from wtforms import Form, StringField, DateTimeField, SelectField, IntegerField
from wtforms import ValidationError
from wtforms.validators import DataRequired, length
from ..models import TravelCard


class CreateTravelCardForm(Form):
    localId = IntegerField('LocalId', validators=[DataRequired()])
    travelLocalId = IntegerField('TravelLocalId', validators=[DataRequired()])
    travelOfDay = IntegerField('TravelOfDay', validators=[DataRequired()])

    def validate_localId(self, field):
        if TravelCard.query.filter_by(localId=field.data).first():
            raise ValidationError('LocalId already exist.')

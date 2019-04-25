from wtforms import Form, StringField, DateTimeField, SelectField, IntegerField
from wtforms import ValidationError
from wtforms.validators import DataRequired, length
from ..models import Travel


class CreateTravelForm(Form):
    localId = IntegerField('LocalId', validators=[DataRequired()])
    startDate = DateTimeField('StartDate', validators=[DataRequired()])
    travelKind = SelectField('TravelKind', coerce=int, choices=[(0, 'foreign'),
                                                                (1, 'domestic')])

    def validate_localId(self, field):
        if Travel.query.filter_by(localId=field.data).first():
            raise ValidationError('LocalId already exist.')


class UpdateTravelForm(Form):
    travelKind = SelectField('TravelKind', coerce=int, choices=[(0, 'foreign'),
                                                                (1, 'domestic')])
    endDate = DateTimeField('EndDate', validators=[])

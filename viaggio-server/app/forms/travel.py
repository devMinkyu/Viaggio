from wtforms import Form, StringField, DateTimeField, SelectField
from wtforms import ValidationError
from wtforms.validators import DataRequired, length


class CreateTravelForm(Form):
    startDate = DateTimeField('StartDate', validators=[DataRequired()])
    travelType = SelectField('TravelType', choices=[('preDomestic', 'preDomestic'),
                                                    ('preForeign', 'preForeign'),
                                                    ('domestic', 'domestic'),
                                                    ('foreign', 'foreign')])
    entireCountry = StringField('EntireCountry', validators=[DataRequired()])


class UpdateTravelForm(Form):
    travelType = SelectField('TravelType', choices=[('preDomestic', 'preDomestic'),
                                                    ('preForeign', 'preForeign'),
                                                    ('domestic', 'domestic'),
                                                    ('foreign', 'foreign')])
    endDate = DateTimeField('EndDate', validators=[])

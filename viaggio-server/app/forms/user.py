from wtforms import Form, StringField, IntegerField
from wtforms import ValidationError
from wtforms.validators import DataRequired, length, Email, EqualTo
from ..models import User


class RegistrationForm(Form):
    email = StringField('Email', validators=[DataRequired(), length(1, 64), Email()])
    name = StringField('Name', validators=[DataRequired(), length(1, 64)])
    passwordHash = StringField('Password', validators=[DataRequired(), length(8, 128),
                                                        EqualTo('passwordHash2')])
    passwordHash2 = StringField('Password2', validators=[DataRequired(), length(8, 128)])

    def validate_email(self, field):
        if User.query.filter_by(email=field.data).first():
            raise ValidationError('Email already registered.')


class ChangePasswordForm(Form):
    oldPasswordHash = StringField('OldPwd', validators=[DataRequired(), length(8, 128)])
    passwordHash = StringField('Password', validators=[DataRequired(), length(8, 128),
                                                        EqualTo('passwordHash2')])
    passwordHash2 = StringField('Password2', validators=[DataRequired(), length(8, 128)])


class ChangeUserInfoForm(Form):
    name = StringField('Name', validators=[DataRequired(), length(1, 64)])
    profileImageUrl = StringField('ProfileImageUrl', validators=[length(0, 512)])


class LoginForm(Form):
    email = StringField('Email', validators=[DataRequired(), length(1, 64), Email()])
    passwordHash = StringField('PasswordHash', validators=[DataRequired(), length(8, 128)])

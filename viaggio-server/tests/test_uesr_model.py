import unittest
import time
from datetime import datetime
from app import create_app, db
from app.models import User


class UserModelTestCase(unittest.TestCase):
    def setUp(self):
        self.app = create_app('testing')
        self.app_context = self.app.app_context()
        self.app_context.push()
        db.create_all()

    def tearDown(self):
        db.session.remove()
        db.drop_all()
        self.app_context.pop()

    def test_passwordHash_setter(self):
        u = User(passwordHash='thisistest')
        self.assertTrue(u.passwordHash is not None)

    def test_valid_token(self):
        u = User(passwordHash='thisistest')
        db.session.add(u)
        db.session.commit()
        self.assertTrue(u.token)

    def test_aws_authentication(self):
        u = User(passwordHash='thisistest')
        db.session.add(u)
        db.session.commit()
        aws_auth = u.get_aws_token()
        self.assertFalse(aws_auth['IdentityId'] is None or aws_auth['Token'] is None)
        self.assertTrue(aws_auth['IdentityId'] and aws_auth['Token'])

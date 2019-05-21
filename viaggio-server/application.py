import os
import sys
from flask_migrate import Migrate, upgrade
from app import create_app, db
from app.models import User


application = create_app(os.getenv('FLASK_CONFIG') or 'default')
migrate = Migrate(application, db)

with application.app_context():
    db.create_all()

if __name__ == "__main__":
    application.run()


@application.cli.command()
def test():
    """Run the unit tests."""
    import unittest
    tests = unittest.TestLoader().discover('tests')
    unittest.TextTestRunner(verbosity=2).run(tests)

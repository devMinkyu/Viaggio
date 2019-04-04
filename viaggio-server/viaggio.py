import os
import sys
from flask_migrate import Migrate, upgrade
from app import create_app, db
from app.models import User


app = create_app(os.getenv('FLASK_CONFIG') or 'default')
migrate = Migrate(app, db)

with app.app_context():
    db.create_all()

if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=5000)


@app.cli.command()
def test():
    """Run the unit tests."""
    import unittest
    tests = unittest.TestLoader().discover('tests')
    unittest.TextTestRunner(verbosity=2).run(tests)

from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from config import config
from flask_cors import CORS

db = SQLAlchemy()

def create_app(config_name):
    app = Flask(__name__)
    CORS(app, resources={ r'/*': {'origins': '*'}}, supports_credentials=True)
    app.config.from_object(config[config_name])
    config[config_name].init_app(app)

    db.init_app(app)

    from .auth import auth as auth_blueprint
    app.register_blueprint(auth_blueprint, url_prefix='/api/v1/auth')

    from .api import api as api_blueprint
    app.register_blueprint(api_blueprint, url_prefix='/api/v1')

    return app

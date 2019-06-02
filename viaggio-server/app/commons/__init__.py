from flask import Blueprint

commons = Blueprint('commons', __name__)

from . import common

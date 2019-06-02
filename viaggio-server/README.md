# Viaggio Server

## Installation

This project was generated with Python3. If you don't have any version of python, you can install from `brew install python3`.
If python3 is installed, you can create virtual envitonment with `python3 -m venv venv`.

## Working with a virtual environment.

You can activate virtual environment with `source venv/bin/activate`.
And deactive with `deactive`.

## Requirement File

You can update package file with `pip freeze >requirements.txt` and can install package with `pip install -r requirements.txt`.

## Development Web Server

`export FLASK_APP=viaggio.py`.
`flask run`.

## Database migrate

You can install database migration package with `pip install flask-migrate`.
Then with `flask db init` command, you can create migrations directory, where all the migration scripts will be stored.
Create an automatic migration script with the `flask db migrate` command.
Apply the migration to the database with the `flask db upgrade` command.
In your alembic env.py file, modify the run_migrations_* methods context.configure segments by adding the `compare_type=True`. If this command is not exist, Column type change cannot detected.

## Envitonment variable

You can set environment variable in .env file. For using .env file, install env module with `pip install pyhton-dotenv`.


## AWS SDK

You can install AWS SDK with `pip install boto3` command. Using this SDK you can get access authentication AWS.


## Deployment

You can deploy server using aws elasticbeanstalk. First, install module globally with `sudo pip install awsebcli` command.
Second, create an elasticbeanstalk repo with `eb init -p python-3.6 my-app --region us-east-1`. This will set up a new environment my-app.
Third, `eb create flask-env -db.engine mysql`.
Finally `eb deploy`.

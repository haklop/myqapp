from flask import Flask, request
from subprocess import call
import json

app = Flask(__name__)

@app.route('/',methods=['POST'])
def post():
    data = json.loads(request.form['payload'])
    if data['ref'] == 'refs/heads/master':
        call("/home/haklop/infoq/myqapp/deploy.sh")
    return "OK"

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=2306)

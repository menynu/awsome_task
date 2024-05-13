from flask import Flask, jsonify
import docker

app = Flask(__name__)
client = docker.DockerClient(base_url='unix://var/run/docker.sock')

@app.route('/')
def home():
    return "Welcome to the Flask App!"
@app.route('/containers')
def list_containers():
    containers = client.containers.list()
    names = [container.name for container in containers]
    return jsonify(names)

if __name__ == '__main__':
    app.run(host='0.0.0.0')

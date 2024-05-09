from flask import Flask

import docker

app = Flask(__name__)
client = docker.from_env()

@app.route('/')
def list_containers():
    containers = client.containers.list()
    container_info = [(container.name, container.short_id) for container in containers]
    return '\n'.join([f'{name}: {id}' for name, id in container_info])

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)

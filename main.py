import docker
from flask import Flask

app = Flask(__name__)

@app.route('/')
def list_containers():
    client = docker.from_env()
    containers = client.containers.list()
    
    container_list = ""
    for container in containers:
        container_list += f"Container ID: {container.id}\n"
        container_list += f"Image: {container.image.tags[0]}\n"
        container_list += f"Status: {container.status}\n\n"
    
    return container_list

if __name__ == '__main__':
    app.run()

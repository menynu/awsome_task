from flask import Flask, jsonify
import docker

app = Flask(__name__)

@app.route('/')
def list_containers():
    try:
        # Explicitly setting the Docker client to use the Unix socket
        client = docker.DockerClient(base_url='unix://var/run/docker.sock')
        containers = client.containers.list()
        
        container_list = []
        for container in containers:
            container_info = {
                "Container ID": container.id,
                "Image": container.image.tags[0] if container.image.tags else "No image tags",
                "Status": container.status
            }
            container_list.append(container_info)

        return jsonify(container_list)
    except Exception as e:
        return jsonify({"error": "Failed to connect to Docker daemon", "details": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)

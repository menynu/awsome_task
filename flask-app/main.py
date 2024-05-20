from flask import Flask, jsonify, request
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

        # Retrieve the X-Real-IP and X-Forwarded-For headers from the request
        x_real_ip = request.headers.get('X-Real-IP', 'Header Not Found')
        x_forwarded_for = request.headers.get('X-Forwarded-For', 'Header Not Found')

        # Include these headers in the response along with the container list
        response = {
            "containers": container_list,
            "X-Real-IP": x_real_ip,
            "X-Forwarded-For": x_forwarded_for
        }
        
        return jsonify(response)
    except Exception as e:
        return jsonify({"error": "Failed to connect to Docker daemon", "details": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)

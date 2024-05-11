import docker
from flask import Flask, jsonify
import logging

app = Flask(__name__)

# Setup basic logging
logging.basicConfig(level=logging.DEBUG, format='%(asctime)s - %(levelname)s - %(message)s')

@app.route('/')
def list_containers():
    try:
        client = docker.from_env()
        containers = client.containers.list()

        container_list = []
        for container in containers:
            container_info = {
                "Container ID": container.id,
                "Image": container.image.tags[0] if container.image.tags else "No tags",
                "Status": container.status
            }
            container_list.append(container_info)
            logging.info(f"Processed container: {container.id}")

        return jsonify(container_list)
    except docker.errors.DockerException as e:
        logging.error(f"Docker error: {str(e)}")
        return jsonify({"error": "Docker error", "details": str(e)}), 500
    except Exception as e:
        logging.error(f"General error: {str(e)}")
        return jsonify({"error": "General error", "details": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
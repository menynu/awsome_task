from flask import Flask, jsonify
import docker
from prometheus_flask_exporter import PrometheusMetrics

app = Flask(__name__)
metrics = PrometheusMetrics(app)

client = docker.DockerClient(base_url='unix://var/run/docker.sock')

# Define the metric outside of the request handling logic
container_gauge = metrics.info('app_containers', 'Number of running containers')

@app.route('/')
def home():
    return "Welcome to the Flask App!"

@app.route('/containers')
def list_containers():
    containers = client.containers.list()
    container_count = len(containers)
    container_gauge.set(container_count)  # Update the gauge with the current container count
    return jsonify([container.name for container in containers])

if __name__ == '__main__':
    app.run(host='0.0.0.0')

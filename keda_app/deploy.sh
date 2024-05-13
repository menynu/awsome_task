#!/bin/bash

# Define variables
FLASK_APP_DOCKERFILE="Dockerfile.flask"
NGINX_DOCKERFILE="Dockerfile.nginx"
DOCKER_USERNAME="meny205"
FLASK_IMAGE_NAME="flask-app-image"
NGINX_IMAGE_NAME="nginx-modified-image"
K8S_DEPLOYMENT_FILE="deployment.yaml"
K8S_SERVICE_FILE="service.yaml"

# Step 1: Build Docker images
echo "Building Flask Docker image..."
docker build -f $FLASK_APP_DOCKERFILE -t $DOCKER_USERNAME/$FLASK_IMAGE_NAME .

echo "Building NGINX Docker image..."
docker build -f $NGINX_DOCKERFILE -t $DOCKER_USERNAME/$NGINX_IMAGE_NAME .

# Step 2: Push Docker images to Docker Hub
echo "Pushing Flask Docker image to Docker Hub..."
docker push $DOCKER_USERNAME/$FLASK_IMAGE_NAME

echo "Pushing NGINX Docker image to Docker Hub..."
docker push $DOCKER_USERNAME/$NGINX_IMAGE_NAME

# Step 3: Deploy to Kubernetes
echo "Deploying to Kubernetes..."
kubectl apply -f $K8S_DEPLOYMENT_FILE
kubectl apply -f $K8S_SERVICE_FILE

echo "Deployment complete. Checking status of pods..."
kubectl get pods

echo "Script completed successfully."
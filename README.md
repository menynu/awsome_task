
This repository contains Jenkins pipeline jobs to build, push, run, verify, and clean up Docker images for a Flask application and a custom Nginx proxy. The pipelines are defined using Jenkins Job DSL.

## Prerequisites

- Jenkins installed and running.
- Docker installed on the Jenkins server.
- Jenkins plugins:
  - Job DSL
  - Git
  - Docker Pipeline
- Jenkins credentials:
  - `github-cred` for accessing the GitHub repository.
  - `dockerhub-cred` for Docker Hub.

## Jenkins Jobs

1. **Build-and-Push-Flask-Docker-List-App**
   - Clones the repository.
   - Builds a Docker image for the Flask application.
   - Pushes the Docker image to Docker Hub.

2. **Build-and-Push-Custom-Nginx**
   - Clones the repository.
   - Builds a custom Nginx Docker image with a proxy configuration.
   - Pushes the Docker image to Docker Hub.

3. **Run-and-Verify-Containers**
   - Sets up a Docker network.
   - Runs the Flask and Nginx containers.
   - Verifies the Nginx proxy to the Flask application.
   - Does not include cleanup (cleanup is handled by a separate job).

4. **Cleanup-Docker-Environment**
   - Stops and removes the Flask and Nginx containers.
   - Removes the Docker network.

## How to Run

1. **Clone the Repository**
   ```bash
   git clone https://github.com/menynu/Elbit_task.git
   cd Elbit_task
def repoUrl = 'https://github.com/menynu/awsome_task.git'
def imageName = 'meny205/flask-docker-list'
def dockerHubCredentialsId = 'dockerhub-cred' 

pipelineJob('Build-and-Push-Flask-Docker-List-App') {
    definition {
        cps {
            script("""
                pipeline {
                    agent any
                    environment {
                        DOCKER_IMAGE = '${imageName}'
                        DOCKER_CREDENTIALS_ID = '${dockerHubCredentialsId}'
                    }
                    stages {
                        stage('Clone Repository') {
                            steps {
                                git(url: '${repoUrl}', credentialsId: 'github-cred')
                            }
                        }
                        stage('Build Docker Image') {
                            steps {
                                script {
                                    sh 'docker build --no-cache -t \$DOCKER_IMAGE .'
                                }
                            }
                        }
                        stage('Push to Docker Hub') {
                            steps {
                                script {
                                    docker.withRegistry('', "\$DOCKER_CREDENTIALS_ID") {
                                        docker.image("\$DOCKER_IMAGE").push('latest')
                                    }
                                }
                            }
                        }
                        stage('Run Docker Container') {
                            steps {
                                script {
                                    sh 'docker run -d --name flask_container -p 5000:5000 -v /var/run/docker.sock:/var/run/docker.sock \$DOCKER_IMAGE'
                                }
                            }
                        }
                    }
                }
            """.stripIndent())
            sandbox(true)
        }
    }
}

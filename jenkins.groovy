def repoUrl = 'https://github.com/menynu/awsome_task.git'
def imageName = 'meny205/flask-docker-list'
def dockerHubCredentialsId = 'dockerhub-cred' // This should match the ID of your Docker Hub credentials in Jenkins

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
                                    def customImage = docker.build("\$DOCKER_IMAGE")
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
                                    sh "docker run -d -p 5000:5000 -v /var/run/docker.sock:/var/run/docker.sock \$DOCKER_IMAGE"
                                }
                            }
                        }
                    }
                }
            """.stripIndent()) // Using stripIndent() instead of trimIndent()
            sandbox(true)
        }
    }
}
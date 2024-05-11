def repoUrl = 'https://github.com/menynu/awsome_task.git'
def flaskImageName = 'meny205/flask-docker-list'
def nginxImageName = 'meny205/nginx-custom'
def dockerHubCredentialsId = 'dockerhub-cred'

// Job for Flask Docker Image
pipelineJob('Build-and-Push-Flask-Docker-List-App') {
    definition {
        cps {
            script("""
                pipeline {
                    agent any
                    environment {
                        DOCKER_IMAGE = '${flaskImageName}'
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
                                    dir('flask-app') { 
                                        sh 'docker build --no-cache -t \$DOCKER_IMAGE .'
                                    }
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
                    }
                }
            """.stripIndent())
            sandbox(true)
        }
    }
}

// Job for Custom Nginx Docker Image
pipelineJob('Build-and-Push-Custom-Nginx') {
    definition {
        cps {
            script("""
                pipeline {
                    agent any
                    environment {
                        DOCKER_IMAGE = '${nginxImageName}'
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
                                    dir('nginx') { 
                                    	sh 'docker build -f Dockerfile --no-cache -t \$DOCKER_IMAGE .'
                                	}
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
                    }
                }
            """.stripIndent())
            sandbox(true)
        }
    }
}

// Job to Run Containers and Verify
pipelineJob('Run-and-Verify-Containers') {
    definition {
        cps {
            script("""
                pipeline {
                    agent any
                    stages {
                        stage('Run Containers') {
                            steps {
                                script {
                                    sh 'docker run -d --name flask_app -p 5000:5000 -v /var/run/docker.sock:/var/run/docker.sock ${flaskImageName}'
                                    sh 'docker run -d --name nginx_app -p 80:80 ${nginxImageName}'
                                }
                            }
                        }
                        stage('Verify Request') {
                            steps {
                                script {
                                    // Small delay to ensure services are up
                                    sleep 15
                                    def response = sh(script: 'curl -s http://localhost', returnStdout: true).trim()
                                    echo "Response from Nginx: \$response"
                                }
                            }
                        }
                        stage('Cleanup') {
                            steps {
                                script {
                                    sh 'docker stop flask_app nginx_app'
                                    sh 'docker rm flask_app nginx_app'
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

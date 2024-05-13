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
                        stage('Setup and Run Containers') {
                            steps {
                                script {
                                    sh 'docker network create --driver bridge isolated_network || true'
                                    sh 'docker run -d --user root --name flask_app --network isolated_network -p 5000:5000 -v /var/run/docker.sock:/var/run/docker.sock ${flaskImageName}'
                                    sh 'docker run -d --name nginx_app --network isolated_network -p 80:80 ${nginxImageName}'
                                }
                            }
                        }
                        stage('Verify Request') {
                            steps {
                                script {
                                    sleep 30
                                    def nginxResponse = sh(script: 'curl -s http://localhost', returnStdout: true).trim()
                                    echo "Response from Nginx: \$nginxResponse"
                                    sh 'docker logs flask_app'  // Get Flask logs after Nginx request
                                    def flaskResponse = sh(script: 'docker exec nginx_app curl -s http://flask_app:5000', returnStdout: true).trim()
                                    echo "Direct response from Flask: \$flaskResponse"
                                    sh 'docker logs flask_app'  // Get Flask logs after direct request
                                }
                            }
                        }
                        stage('Cleanup') {
                            steps {
                                script {
                                    sh 'docker stop flask_app nginx_app || true'
                                    sh 'docker rm flask_app nginx_app || true'
                                    sh 'docker network rm isolated_network || true'
                                }
                            }
                        }
                    }
                    post {
                        always {
                            script {
                                sh 'docker stop flask_app nginx_app || true'
                                sh 'docker rm flask_app nginx_app || true'
                                sh 'docker network rm isolated_network || true'
                            }
                        }
                    }
                }
            """.stripIndent())
            sandbox(true)
        }
    }
}


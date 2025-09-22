pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = "sie-backend"
        DOCKER_CREDENTIALS = 'dockerhub-creds'
        DOCKER_USERNAME = 'dmaldonado'
        SERVER_IP = '20.55.81.100'
        SERVER_PATH = '/opt/springboot-app'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    echo "Checking out branch"
                    env.GIT_BRANCH = sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()
                }
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    echo "Building Docker image for branch: ${env.GIT_BRANCH}"
                    docker.build("${DOCKER_IMAGE}:${BUILD_NUMBER}")
                }
            }
        }
        
        stage('Push to Docker Hub') {
            when {
                branch 'main'
            }
            steps {
                script {
                    echo "Pushing Docker image to Docker Hub"
                    docker.withRegistry('https://index.docker.io/v1/', DOCKER_CREDENTIALS) {
                        sh "docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_USERNAME}/${DOCKER_IMAGE}:${BUILD_NUMBER}"
                        sh "docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_USERNAME}/${DOCKER_IMAGE}:latest"
                        sh "docker push ${DOCKER_USERNAME}/${DOCKER_IMAGE}:${BUILD_NUMBER}"
                        sh "docker push ${DOCKER_USERNAME}/${DOCKER_IMAGE}:latest"
                    }
                }
            }
        }
        
        stage('Deploy with Docker Compose') {
            when {
                branch 'main'
            }
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'ssh-credentials',
                        usernameVariable: 'SSH_USER',
                        passwordVariable: 'SSH_PASSWORD'
                    )
                ]) {
                    echo "Deploying to server ${SERVER_IP}"
                    sh """
                        sshpass -p "\${SSH_PASSWORD}" ssh -o StrictHostKeyChecking=no \${SSH_USER}@${SERVER_IP} "
                            cd ${SERVER_PATH} &&
                            echo VERSION=${BUILD_NUMBER} > .env &&
                            docker-compose pull &&
                            docker-compose down &&
                            docker-compose up -d
                        "
                    """
                }
            }
        }
    }
    
    post {
        always {
            sh 'docker system prune -f || true'
        }
        success {
            echo "Pipeline successful for branch: ${env.GIT_BRANCH}"
        }
        failure {
            echo "Pipeline failed for branch: ${env.GIT_BRANCH}"
        }
    }
}
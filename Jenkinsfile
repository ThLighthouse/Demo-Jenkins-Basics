pipeline {
    agent any
    tools {
        maven 'maven-3.9'
    }
    stages {
        stage('Build jar') {
            steps {
                script {
                    echo "Building jar file..."
                    sh 'mvn package'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    echo "Building Docker image..."

                    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', usernameVariable: 'USER', passwordVariable: 'PASSWORD')]) {
                        sh 'docker build -t thlighthouse/demo-app:jma-2.0 .'
                        sh 'echo $PASSWORD | docker login -u $USER --password-stdin'
                        sh 'docker push thlighthouse/demo-app:jma-2.0'
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo "Deploying the application..."
                }
            }
        }
    }
}
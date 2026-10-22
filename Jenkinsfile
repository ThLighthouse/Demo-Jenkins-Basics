pipeline {
    agent any
    tools {
        maven 'maven-3.9'
    }
    
    stages { 
        stage ('increment version') {
            steps {
                echo 'Incrementing the app version...'
                
                sh 'mvn build-helper:parse-version versions:set \
                    -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
                    versions:commit'
                
                script {
                    def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                    def version = matcher[0][1]
                    env.IMAGE_NAME = "${version}-${env.BUILD_NUMBER}"
                
                    echo "Docker image version: ${env.IMAGE_NAME}"
                }    
            }
        }

        stage('Build application') {
            steps {
                echo 'Building the application...'
                sh 'mvn clean package'
            }
        }
    

        stage('Build image') {
            steps {
                script {
                    echo "Building Docker image..."
                    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', usernameVariable: 'USER', passwordVariable: 'PASSWORD')]) {
                    sh "docker build -t thlighthouse/demo-app:${env.IMAGE_NAME} ."
                    sh 'echo $PASSWORD | docker login -u $USER --password-stdin'
                    sh "docker push thlighthouse/demo-app:${env.IMAGE_NAME}"
                    }
                }
            }
        }

        stage('Deploy application') {
            steps {
                script {
                    echo "Deploying docker image..."                }
            }
        }

        stage('commit version update') {
            steps {
                script {
                        sh 'git config user.email "jenkins@example.com"'
                        sh 'git config user.name "Jenkins"'

                        sh 'git status'
                        sh 'git branch'
                        sh 'git config --list'

                        withCredentials([gitUsernamePassword(
                            credentialsId: 'github-credentials',
                            gitToolName: 'git',
                        )]) {
                        sh 'git add pom.xml'
                        sh 'git commit -m "ci: increment version"'
                        sh 'git push origin HEAD:jenkins-jobs'
                    }
                }
            }
        }
    }
}
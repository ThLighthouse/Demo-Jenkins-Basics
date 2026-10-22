pipeline {
    agent any
    tools {
        maven 'Maven'
    }
    
    stages { 
        stage ('increment version') {
            steps {
                echo 'Incrementing the app version...'
                sh 'mvn build-helper:parse-version versions:set \
                    -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
                    versions:commit'
                def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
                def version = matcher[0][1]
                env.IMAGE_NAME = "$version-$BUILD_NUMBER"
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
                    sh "docker build -t thlighthouse/demo-app:${IMAGE_NAME} ."
                    sh 'echo $PASSWORD | docker login -u $USER --password-stdin'
                    sh "docker push thlighthouse/demo-app:${IMAGE_NAME}"
                    }
                }
            }
        }

        stage('Deploy application') {
            steps {
                echo "Deploying the application..."
            }
        }
    }
}
def buildJar() {
    echo 'building the application...'
    sh 'mvn package'
}

def buildImage() {
    echo "Building Docker image..."
    withCredentials([usernamePassword(credentialsId: 'docker-hub-repo', usernameVariable: 'USER', passwordVariable: 'PASSWORD')]) {
        sh 'docker build -t thlighthouse/demo-app:jma-2.0 .'
        sh 'echo $PASSWORD | docker login -u $USER --password-stdin'
        sh 'docker push thlighthouse/demo-app:jma-2.0'
    }
}

def deployApp() {
    echo "Deploying the application..."
}

return this
pipeline {
    agent any
    parameters {
        string(name: 'VERSION', defaultValue: '1.0.0', description: 'Version of the application')
        choice(name: 'VERSION', choices: ['1.0.0', '1.1.0', '2.0.0'], description: 'Select the version to deploy')
        booleanParam(name: 'executeTests', defaultValue: true, description: 'Execute tests before deployment')
    }
    environment {
        NEW_VERSION = params.VERSION
        SERVER_CREDENTIALS = credentials('docker-hub-repo')
    }

    stages {
        stage('Build') {
            steps {
                echo 'Building the application...'
            }
        }

        stage('Test') {
            when {
                expression {
                    params.executeTests == true
                }
            }
            steps {
                echo 'Testing the application...'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying the application...'
                echo "Deploying version ${params.VERSION} to the server..."
            }
        }
    }
}
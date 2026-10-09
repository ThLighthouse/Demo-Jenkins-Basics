pipeline {
    agent any
    parameters {
        choice(name: 'VERSION', choices: ['1.0.0', '1.1.0', '2.0.0'], description: 'Select the version to deploy')
        booleanParam(name: 'executeTests', defaultValue: true, description: 'Execute tests before deployment')
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
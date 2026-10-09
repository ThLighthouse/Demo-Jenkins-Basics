def gv

pipeline {
    agent any
    parameters {
        choice(name: 'VERSION', choices: ['1.0.0', '1.1.0', '2.0.0'], description: 'Select the version to deploy')
        booleanParam(name: 'executeTests', defaultValue: true, description: 'Execute tests before deployment')
    }

    stages {
        stage('init') {
            steps {
                script {
                    gv = load 'script.groovy'
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    gv.buildApp()
                }
            }
        }

        stage('Test') {
            when {
                expression {
                    params.executeTests == true
                }
            }
            steps {
                script {
                    gv.testApp()
                }
            }
        }

        stage('Deploy') {
            input {
                message "Select the environment to deploy to"
                ok "done"
                parameters{
                    choice(name: 'ONE', choices: ['dev', 'staging', 'production'], description: 'Select the environment to deploy to')
                    choice(name: 'TWO', choices: ['dev', 'staging', 'production'], description: 'Select the environment to deploy to')

                }
            }
            steps {
                script {
                    gv.deployApp()
                    echo "Deploying to ${ONE}"
                    echo "Deploying to ${TWO}"
                }
            }
        }
    }
}
def gv

pipeline {
    agent any
    tools {
        maven 'maven-3.9'
    }
    stages {
        stage('init') {
            steps {
                script {
                    gv = load 'script.groovy'
                }
            }
        }
        stage('Build jar') {
            steps {
                script {
                    gv.buildJar()
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    gv.buildImage()
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    gv.deployApp()
                    sh 'echo "TEST: GITHUB -> JENKINS INTEGRATION with MULTIBRANCH-PIPELINE"'
                }
            }
        }
    }
}
#!/usr/bin/env groovy

library identifier: 'jenkins-shared-library@master', retriever: modernSCM(
    [$class: 'GitSCMSource',
        remote: 'https://gitlab.com/alisher.khusainov/jenkins-shared-library.git',
        credentialsId: 'gitlab-credentials'])

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
                    buildJar()
                }
            }
        }

        stage('Build and push image') {
            steps {
                script {
                    buildImage 'thlighthouse/demo-app:jma-3.0'
                    dockerLogin()
                    dockerPush 'thlighthouse/demo-app:jma-3.0'
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    gv.deployApp()
                }
            }
        }
    }
}
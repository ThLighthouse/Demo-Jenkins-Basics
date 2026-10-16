#!/usr/bin/env groovy

@Library('jenkins-shared-library@master')
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

        stage('Build Docker Image and push') {
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
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

        stage('Build Docker Image') {
            steps {
                script {
                    buildImage()
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
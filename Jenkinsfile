pipeline {
    agent any
    tools {
        jdk 'jdk11'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('PMD') {
            steps {
                 sh './gradlew pmdMain'
            }
        }
        stage('Run tests') {
             steps {
                  sh './gradlew :cleanTest :test'
             }
        }

    }
}

pipeline {
    agent any
    tools {
        jdk 'jdk11'
    }
    environment {
        DATABASE_URL = 'jdbc:postgresql://localhost:5432/books'
        DATABASE_USERNAME = credentials('books-database-username')
        DATABASE_PASSWORD = credentials('books-database-password')
        BOOKS_MAIL_HOST = 'smtp.mail.yahoo.com'
        BOOKS_MAIL_FROM = credentials('books-mail-from')
        BOOKS_MAIL_USERNAME = credentials('books-mail-username')
        BOOKS_MAIL_PASSWORD = credentials('books-mail-password')
        BOT_USERNAME = credentials('quotes-bot-username')
        BOT_PASSWORD = credentials('quotes-bot-password')
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
        stage('Build') {
            steps {
                sh './gradlew bootJar'
            }
        }
        stage('Run tests') {
            environment {
                BOOKS_LOGS_PATH = '/Users/julia/logs/books'
            }
            steps {
                sh './gradlew :cleanTest :test'
            }
        }

        stage('Build docker image') {
            steps {
                sh 'docker build -t shadowofhobbit/books-service .'
            }
        }
        stage('Run') {
            steps {
                sh 'docker run --rm -p 8080:8080 -d \
          --env DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/books --env DATABASE_USERNAME --env DATABASE_PASSWORD \
          --env BOOKS_MAIL_HOST --env BOOKS_MAIL_FROM --env BOOKS_MAIL_USERNAME --env BOOKS_MAIL_PASSWORD \
          --env BOT_USERNAME --env BOT_PASSWORD --env BOOKS_LOGS_PATH=/home/spring/app/logs \
          -v /Users/julia/logs/books:/home/spring/app/logs shadowofhobbit/books-service'
            }
        }

    }
}

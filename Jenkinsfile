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
        BOOKS_LOGS_PATH = '/Users/julia/logs/books'
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
        stage('Run') {
            steps {
                sh './gradlew bootRun'
            }
        }

    }
}

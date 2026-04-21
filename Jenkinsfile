pipeline {
    agent any

    environment {
        // Usa le credenziali salvate su Jenkins per il file .env
        POSTGRES_USER = credentials('db-user')
        POSTGRES_PASSWORD = credentials('db-password')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/lor07enzo/Holiday-Booking-System.git'
            }
        }

        stage('Build & Run Docker') {
            steps {
                script {
                    sh 'echo "POSTGRES_USER=$POSTGRES_USER" > .env'
                    sh 'echo "POSTGRES_PASSWORD=$POSTGRES_PASSWORD" >> .env'
                    
                    // Comandi docker
                    sh 'docker-compose down'
                    sh 'docker-compose up -d --build'
                }
            }
        }
        
        stage('Health Check') {
            steps {
                sh 'docker ps'
                echo "Applicazione lanciata con successo!"
            }
        }
    }
}
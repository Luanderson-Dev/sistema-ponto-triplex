pipeline {
    agent any

    environment {
        PROJECT_DIR = '/apps/ponto-da-triplex'
    }

    stages {
        stage('Pull Latest Code') {
            steps {
                sh """
                    cd ${PROJECT_DIR}
                    git pull origin main
                """
            }
        }

        stage('Build & Deploy') {
            steps {
                sh """
                    cd ${PROJECT_DIR}
                    docker compose build --no-cache
                    docker compose up -d
                """
            }
        }

        stage('Cleanup') {
            steps {
                sh 'docker image prune -f'
            }
        }
    }

    post {
        failure {
            echo 'Deploy falhou!'
        }
        success {
            echo 'Deploy realizado com sucesso!'
        }
    }
}

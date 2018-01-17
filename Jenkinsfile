pipeline {
    agent any
    stages {
        stage('Step 1 - Build') {
            steps {
                sh 'git clone github.com/Gradergage/JsonValidator'
                echo 'Step 1 executed, Pokatilo P.A.'
            }
        }
        stage('Step 2 - gradle build') {
            steps {
                sh 'chmod +x gradlew'
		sh './gradlew createDockerImage'
                echo 'Step 2 executed, Pokatilo P.A.'
            }
        }
        stage('Step 3 - info') {
            steps {
                echo 'Step 3 executed, Pokatilo P.A.'
                echo 'To validate file use: curl -s --upload-file filename.json http://localhost'
            }
        }
    }
}



pipeline {
  agent {
    kubernetes {
      label 'mypod'
      containerTemplate {
        name 'maven'
        image 'maven:3.5.2-jdk-8'
        ttyEnabled true
        command 'cat'
      }
    }
  }
  stages {
    stage('build') {
      steps {
        container('maven') {
          sh 'mvn -B clean package -DskipTests'
          stash name: 'build', includes: 'pom.xml, src/, target/'
        }
      }
    }

    stage('test') {

      steps {
        container('maven') {
          unstash 'build'
          sh 'mvn -B test -Dmaven.test.failure.ignore'
        }
      }
    }
  }
}

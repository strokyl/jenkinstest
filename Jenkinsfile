pipeline {
  agent {
    docker {
      image 'maven:3.5.2-jdk-8'
    }
    
  }
  stages {
    stage('build') {
      steps {
        sh '''mvn test-compile'''
      }
    }

    stage('test') {
      steps {
        script {
          stash name: 'sources', includes: 'pom.xml, src/'
          def splits = splitTests parallelism: [$class: 'CountDrivenParallelism', size: 4], generateInclusions: true
          def testGroups = [:]
          for (int i = 0; i < splits.size(); i++) {
            def j = i
            def split = splits[j]

            testGroups["split-${j}"] = {
              node {
                unstash 'sources'
                def mavenTest = 'mvn test -Dmaven.test.failure.ignore'

                echo split.toString()
                if (split.list.size() > 0) {
                  if (split.includes) {
                    writeFile file: "target/parallel-test-includes-${j}.txt", text: split.list.join("\n")
                    sh "cat target/parallel-test-includes-${j}.txt"
                    mavenTest += " -Dsurefire.includesFile=target/parallel-test-includes-${j}.txt"
                  } else {
                    writeFile file: "target/parallel-test-excludes-${j}.txt", text: split.list.join("\n")
                    sh "cat target/parallel-test-excludes-${j}.txt"
                    mavenTest += " -Dsurefile.excludesFile=target/parallel-test-excludes-${j}.txt"
                  }
                }
                else {
                  echo "No split"
                }

                sh mavenTest

                sh "ls target/surefire-reports/TEST-*.xml"
                step([$class: "JUnitResultArchiver", testResults: '**/target/surefire-reports/TEST-*.xml', keepLongStdio: true])
                echo "YOLO"
              }
            }
          }

          parallel testGroups
        }
      }
    }
  }
}

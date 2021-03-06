pipeline {
  agent {
    docker {
      image 'maven:3.5.2-jdk-8'
      args '-v /root/.m2:/root/.m2'
    }
  }
  stages {
    stage('build') {
      steps {
        sh 'mvn -B clean package -DskipTests'
        stash name: 'build', includes: 'pom.xml, src/, target/'
      }
    }

    stage('test') {

      steps {
        script {
          def splits = splitTests parallelism: [$class: 'CountDrivenParallelism', size: 4], generateInclusions: true
          def testGroups = [:]
          for (int i = 0; i < splits.size(); i++) {
            def j = i
            def split = splits[j]

            testGroups["split-${j}"] = {
              node {
                unstash 'build'
                def mavenTest = 'mvn -B test -Dmaven.test.failure.ignore'

                if (split.list.size() > 0) {
                  if (split.includes) {
                    def includes = split.list.join("\n")
                    sh "echo \"${includes}\" >> target/parallel-test-includes-${j}.txt"

                    mavenTest += " -DincludesFile=target/parallel-test-includes-${j}.txt"
                  } else {
                    def excludes = split.list.join("\n")
                    sh "echo \"${excludes}\" >> target/parallel-test-excludes-${j}.txt"

                    mavenTest += " -DexcludesFile=target/parallel-test-excludes-${j}.txt"
                  }
                }

                sh mavenTest

                sh "find . -name TEST-*.xml"
                //stash name: "report-${j}", includes: '**/*.java'
              }
            }
          }

          parallel testGroups

          for (int i = 0; i < splits.size(); i++) {
            //unstash "report-${i}"
          }

          sh "find . -name TEST-*.xml"
          step([$class: "JUnitResultArchiver", testResults: '**/target/surefire-reports/TEST-*.xml'])
        }
      }
    }
  }
}

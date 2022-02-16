# Here, using declarative method to implement code in all stages from code commit to deployment of code 
pipeline {
    agent {
         node { label " helloworld "}
    triggers { 
        pollSCM('*/20 * * * *')
    }
    parameters {
        string(name: 'MAVEN_GOAL', defaultValue: 'package', description: 'This is maven goal' )
        choice(name: 'BRANCH_TO_BUILD', choices: ['declarative', 'master', 'scripted'], description: 'Branch to build')
    }
    options{
        timeout (time: 10, unit: "MINUTES" )
    }
parallel{   
 stages {
        stage('scm') {
          when { 
             branch ' master'
        }
            steps {

                git url: 'https://github.com/GitPracticeRepo/java11-examples.git', branch: "${params.BRANCH_TO_BUILD}"
            }
        }
        stage('build') {
            steps {
                withSonarQubeEnv('SONAR_9.2.1') {
                    sh "/usr/local/apache-maven-3.8.4/bin/mvn ${params.MAVEN_GOAL}"
                    sh "/usr/local/apache-maven-3.8.4/bin/mvn sonar:sonar -Dsonar.login=
                }
                
            }
        }
    }
}
    stage('deploying') {
            
            steps {
              deploy adapters: [tomcat8(url: 'http://localhost:8666/',  // For Deployment purpose
                              credentialsId: 'tomcat')], 
                     war: 'target/*.war',
                     contextPath: 'app'
                }
            }
     
    post {
        always {
            

                emailext attachLog: true,
                body: " the job is successfully built and deployed "  // For Notification after getting output
                compressLog: true,
                replyTo: "do-not-reply@qt.com",
                to: "qtdevops@gmail.com",
                subject: "${env.JOB_NAME} - Build ${env.BUILD_NUMBER} -Status ${currentBuild.result}"


        }
    }
}

 we have number of choices to achieve our goals in pipeline such as using ansible and docker instead of using deploy to container plugin 
 here jenkins uses groovy library of java so we can use error handling option  for eliminating the bugs during build process using try and catch
 blocks 
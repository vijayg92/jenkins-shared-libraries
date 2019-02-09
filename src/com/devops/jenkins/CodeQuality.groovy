package com.devops.jenkins

class CodeQuality {

    private String sonarArgs = '-Dsonar.sources=src/main/java/ -Dsonar.projectKey=org.sonarqube:PaymentCard -Dsonar.projectName=PaymentCard -Dsonar.projectVersion=1.0.0  -Dsonar.java.binaries=.'

    static def runSonarScan(sonarArgs){
    def sonarHome = tool 'sonar'
        withSonarQubeEnv(sonarHome) {
            def returnStatus = script.sh returnStdout: true, script: """${sonarHome}/bin/sonar-scanner -X ${sonarArgs}"""
        }
    }
}

package com.devops.jenkins

class CodeQuality implements Serializable {

    def steps 

    CodeQuality(steps){
        this.steps = steps
    }

    def scanWithSonar(productName, sonarCubeURL, appVersion) {
        
	if (!productName) throw new IllegalArgumentException("The parameter 'mvnConfigFilePath' can not be null or empty.")
        if (!sonarCubeURL) throw new IllegalArgumentException("The parameter 'sonarCubeURL' can not be null or empty.")
        if (!appVersion) throw new IllegalArgumentException("The parameter 'appVersion' can not be null or empty.")

        def sonar = steps.tool name: 'sonar', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
        
        steps.withSonarQube('SonarInstance') {
            steps.sh """
                ${sonar}/bin/sonar-runner -e \\
                -Dsonar.projectKey=org.sonarqube:${productName} \\
                -Dsonar.host.url=${sonarCubeURL} \\
                -Dsonar.projectName=${productName} \\
                -Dsonar.projectVersion=${appVersion} \\
                -Dsonar.sourceEncoding=UTF-8 \\
    	        -Dsonar.java.binaries=.
            """
        }
    }
}

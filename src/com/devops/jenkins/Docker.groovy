package com.devops.jenkins

class Docker {
    
    private String dockerRegistry
    private String registryUser
    private String appGroup
    private String appName
    private String imageTag
    
    def publishAppImage(dockerRegistry, registryUser, appGroup, appName, imageTag){
          
        try {
            docker.withRegistry(dockerRegistry, registryUser) {
                def appImagePath = dockerRegistry + '/' + appGroup + '/' + appName + ':' + imageTag
                def buildImage = docker.build(appImagePath)
                buildImage.push(appImagePath)
            }
        } catch (e) {
            echo "Failed to Build Docker Image : ${e}"
            throw e
        }
    }
}

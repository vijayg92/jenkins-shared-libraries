package com.devops.jenkins

class AppBuilds implements Serializable {
    
    static def buildWithMaven(script, mvnConfigFilePath){
        if (!mvnConfigFilePath) throw new IllegalArgumentException("The parameter 'mvnConfigFilePath' can not be null or empty.")
        try {
            def mvnHome = tool 'maven-3.3.9'
            withMaven(maven: mvnHome, mavenSettingsConfig: mvnConfigFilePath) {
                def returnStatus = script.sh returnStdout: true, script: """${mvnHome}/bin/mvn -X -e ${mvnConfigFilePath} clean install"""
            }
        } catch(err) {
            throw(err)
        }
    }

    def buildWithGradle() {
    
    }

    def buildWithGrunt(){
    
    }

    def buildWithNpm(){
    
    }

    def buildWithPip(){
    
    }

}

package com.devops.jenkins

class AppBuilds {
    
    static def buildWithMaven(script, mvnConfigFilePath, args){
        if (!mvnConfigFilePath) throw new IllegalArgumentException("The parameter 'mvnConfigFilePath' can not be null or empty.")
        try {
		    script.sh "${script.tool 'maven'}/bin/mvn -s ${mvnConfigFilePath} -o ${args}"
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

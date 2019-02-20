package com.devops.jenkins

class AppBuilds implements Serializable {

    def steps 

    AppBuilds(steps){
        this.steps = steps
    }

    def buildWithMaven(mvnArgs){
        def mvn = steps.tool 'maven'
        steps.withMaven(maven: 'maven', mavenLocalRepo: '.m2'){
            steps.sh "${mvn}/bin/mvn -o ${mvnArgs}"
        }
    }

    def buildWithGradle(){

    }

    def buildWithGrunt(){

    }
}

package com.devops.jenkins

class KubeDeploy implements Serializable {
    
    def steps 

    KubeDeploy(steps){
        this.steps = steps
    }

    def deployOnDevCluster() {
        try{
            script.sh ""
        } catch (err) {
            throw err
        }
    }

    def deployOnPreProdCluster() {
        try{
            script.sh ""
        } catch (err) {
            throw err
        }
    }

    def deployOnProdCluster(){
        try{
            script.sh ""
        } catch (err) {
            throw err
        }
    }
}

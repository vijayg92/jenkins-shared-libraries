package com.devops.jenkins

class CodeQuality {

    static def runSonarScan(script, args){
        if (!args) throw new IllegalArgumentException("The parameter 'args' can not be null or empty.")
        try {
		    script.sh "${script.tool 'sonar'}/bin/sonar-scanner -X ${args}"
        } catch(err) {
            throw(err)
        }
    }
}

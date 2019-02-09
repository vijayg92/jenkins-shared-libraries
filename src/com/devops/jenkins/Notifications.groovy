package com.devops.jenkins

class Notifications {

    private String buildStatus
    private String slackChannel
    private String approvers
    private String developers
    private String devops
    private String devTeamEmails
    private String approversEmails
    private String devOpsTeamEmails

    def notifyDeveloperBySlack(buildStatus, slackChannel){
        // build status of null means successful
        buildStatus =  buildStatus ?: 'STARTED'
        def colorCode = '#FF0000'
        def summary = "${buildStatus}: `${env.JOB_NAME}` #${env.BUILD_NUMBER}:\n${env.BUILD_URL}"


        try {
            if (buildStatus == 'STARTED' || buildStatus == 'UNSTABLE') {
                colorCode = '#FFFF00' // YELLOW
            } else if (buildStatus == 'SUCCESS') {
                colorCode = '#00FF00' // GREEN
            } else {
                colorCode = '#FF0000' // RED
            }
            // Send slack notifications all messages
            slackSend (color: colorCode, message: summary, channel: slackChannel)
        } catch (e) {
            echo "Failed to send Slack notification : ${e}"
            throw e
        }
    }

    def notifyApproversBySlack(slackChannel, approvers){
        def colorCode = '#FF0000'
        def summary = "Deployment Approval for : `${env.JOB_NAME}` #${env.BUILD_NUMBER}:\n${env.BUILD_URL}"

        try {
            input (message: "Deployment Approval?", ok: 'Approve', submitter: approvers, submitterParameter: submitter)
            slackSend (color: colorCode, message: summary, channel: slackChannel)
        } catch (e) {
            echo "Failed to send Slack notification : ${e}"
            throw e
        }
    }


    def notifyDevOpsOnSlack(slackChannel, devops) {
        def colorCode = '#FF0000'
        def summary = "Deployment Approval for : `${env.JOB_NAME}` #${env.BUILD_NUMBER}:\n${env.BUILD_URL} to ${devops}"

        try {
            input (message: "Deployment Approval?", ok: 'Approve', submitter: approvers, submitterParameter: submitter)
            slackSend (color: colorCode, message: summary, channel: slackChannel)
        } catch (e) {
            echo "Failed to send Slack notification : ${e}"
            throw e
        }

    }


    def notifyDevByEmail(devTeamEmails) {
        def summary = "CP DevOps Jenkins - ${JOB_BASE_NAME} - Build #${BUILD_NUMBER} - Dev Build Inputs"
        def details = """
                      <html>
                        <body>
                            Hello Dev Team,
                            <br><br><br>
                            You are receiving this email because the Build <a href="${BUILD_URL}">${BUILD_NUMBER}</a> developer inputs to proceed the build further.
                            <br><br>
                            Click <a href="${BUILD_URL}input">here</a> to provide neccesary build parameters for ${BUILD_NUMBER} release deployment. 
                            <br><br><br>
                            Regards,<br>
                            CP DevOps Team<br>
                        </body>
                      </html>
                      """
        try {
            emailext(subject: summary, body: details, mimeType: 'text/html', to: devTeamEmails)
        } catch (e) {
            echo "Failed to send Email notification : ${e}"
            throw e
        }
    }

    def notifyApproversByEmail(approversEmails) {
        def summary = "CP DevOps Jenkins - ${JOB_BASE_NAME} - Build #${BUILD_NUMBER} - Deployment Approval"
        def details = """\
                      <html>
                        <body>
                             Hello there,
                             <br><br><br>
                             You are receiving this email because the Build <a href="${BUILD_URL}">${BUILD_NUMBER}</a> requires an approval to proceed further.
                             <br><br>
                             Click <a href="${BUILD_URL}input">here</a> if you want to <b>approve</b> or <b>abort</b> ${BUILD_NUMBER} release deployment. 
                             <br><br><br>
                             Regards,<br>
                             CP DevOps Team<br>
                        </body>
                      </html>
                      """
        try{
            emailext (subject: summary, body: details, mimeType: 'text/html', to: approversEmails)
        } catch (e) {
            echo "Failed to send Email notification : ${e}"
            throw e
        }
    }

    def notifyDevOpsByEmail(devOpsTeamEmails) {
        def summary = "CP DevOps Jenkins - ${JOB_BASE_NAME} - Build #${BUILD_NUMBER} - Deployment Approval"
        def details = """\
                    <html>
                        <body>
                            Hello DevOps Team,
                            <br><br><br>
                            You are receiving this email because the Build <a href="${BUILD_URL}">${BUILD_NUMBER}</a> requires an approval to proceed further.
                            <br><br>
                            Click <a href="${BUILD_URL}input">here</a> if you want to <b>approve</b> or <b>abort</b> ${BUILD_NUMBER} release deployment. 
                            <br><br><br>
                            Regards,<br>
                            CP DevOps Team<br>
                        </body>
                    </html>
                     """
        try{
            emailext (subject: summary, body: details, mimeType: 'text/html', to: devOpsTeamEmails)
        } catch (e) {
            echo "Failed to send Email notification : ${e}"
            throw e
        }

    }
}
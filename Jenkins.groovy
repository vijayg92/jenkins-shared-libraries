import com.devops.jenkins.AppBuilds
import com.devops.jenkins.CodeQuality
import com.devops.jenkins.Openshiftv3
import com.devops.jenkins.Jira
import com.devops.jenkins.Notifications

def notifyUtils = new Notifications()
def buildUtils = new AppBuilds()

def developers_slack = "#search-ci-devs"
def approvers_slack = "#search-ci-releases"
def devops_slack = "#ci-releases-devops"
def approvers = "approver1, approver2, approver3, approver4"
def dev_team = "mydev@redhat.com"

try {

    node('paas') {

        try{

            notifyUtils.notifyDeveloperBySlack(currentBuild.result, developers_slack)

            stage("Build") {

            }

            stage("Test"){

            }

            stage("Package"){

            }

            stage("Publish"){

            }

            stage("Deploy on Dev"){

            }

            stage("Test Dev"){

            }

            stage("QA Approval"){
                notifyUtils.notifyApproversBySlack(approvers_slack, approvers)
            }

            stage("Deploy on Stage"){

            }

            stage("Test Stage") {
            }

            stage("QA Approval"){
                notifyUtils.notifyApproversBySlack(approvers_slack, approvers)
            }

            stage("DevOps Approval"){
                notifyUtils.notifyDevOpsOnSlack(devops_slack, devops_team)
            }
            stage("Deploy on Prod"){

            }

            stage("Test Prod"){

            }

            currentBuild.result = "SUCCESS"

        } catch (org.jenkinsci.plugins.workflow.steps.FlowInterruptedException ex) {
            currentBuild.result = "UNSTABLE"
            throw ex

        } catch(err) {
            currentBuild.result = "FAILED"
            throw err

        } finally {
            notifyUtils.notifyDevByEmail(currentBuild.result, dev_team)
            notifyUtils.notifyDeveloperBySlack(currentBuild.result, developers_slack)
        }
    }

} catch (e) {
    throw e
}


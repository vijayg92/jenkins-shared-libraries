package com.redhat.jenkins.pipeline

import groovy.json.JsonSlurper

class Jira {


    @NonCPS
    def createIssue(Map settings) {
        def requiredFields = ['project', 'reporter', 'appname', 'version', 'issuetype']

        def missingFields = []
        for (int i = 0; i < requiredFields.size(); i++) {
            def field = requiredFields[i]
            if (!settings.containsKey(field)) {
                missingFields.add(field)
            }
        }
        if (missingFields.size() > 0) {
            throw new java.security.InvalidParameterException("createIssue() missing field(s): " + missingFields.join(', '))
        }

        def baseJiraUrl = 'https://projects.engineering.redhat.com/'
        def apiUrl = baseJiraUrl + 'rest/api/2/issue'
        def dateToday = new Date().format('yyyy.MM.dd')
        def summary = "CP DevOps Release Deployment of ${settings.appname} Application"
        def description = "Automated Release Deployment Jira:\\nApplication: ${settings.appname}\\nVersion: ${settings.version}\\nStarted by: ${settings.reporter}\\nJenkins Build: ${env.BUILD_URL}"
        def request = """\
            {
            "fields": {
                "project": {
                    "key": "${settings.project}"
                },
                "issuetype": {
                    "name": "${settings.issuetype}"
                },
                "assignee": {
                    "name": "cp-devops-jira"
                },
                "summary": "${summary}",
                "labels": ["${settings.appname}"],
                "description": "${description}"
            }
            }
        """
        echo "Request:\n${request}"
        def response = httpRequest(authentication: 'jira', timeout: 30, contentType: 'APPLICATION_JSON', httpMode: 'POST', ignoreSslErrors: true, requestBody: request, url: apiUrl, validResponseCodes: '201')
        println(response)
        def jsonParser = new JsonSlurper()
        def output = jsonParser.parseText(response.content)
        def issue = output.key
        return issue
    }

    @NonCPS
    def assignIssue(Map settings) {
        def requiredFields = ['issue', 'assignee']

        def missingFields = []
        for (int i = 0; i < requiredFields.size(); i++) {
            def field = requiredFields[i]
            if (!settings.containsKey(field)) {
                missingFields.add(field)
            }
        }
        if (missingFields.size() > 0) {
            throw new java.security.InvalidParameterException("deploymentInProgress() missing field(s): " + missingFields.join(', '))
        }

        def baseJiraUrl = 'https://projects.engineering.redhat.com'
        def apiUrl = baseJiraUrl + '/rest/api/2/issue/' + settings['issue'] + '/assignee'
        def request = """\
      {
        "name": "${settings.assignee}"
      }
      """
        echo "Request:\n${request}"

        httpRequest(authentication: 'jira', timeout: 30, contentType: 'APPLICATION_JSON', httpMode: 'PUT', ignoreSslErrors: true, requestBody: request, url: apiUrl, validResponseCodes: '204')
    }

    @NonCPS
    def addIssueComment(Map settings) {
        def requiredFields = ['issue', 'comment']

        def missingFields = []
        for (int i = 0; i < requiredFields.size(); i++) {
            def field = requiredFields[i]
            if (!settings.containsKey(field)) {
                missingFields.add(field)
            }
        }
        if (missingFields.size() > 0) {
            throw new java.security.InvalidParameterException("addIssueComment() missing field(s): " + missingFields.join(', '))
        }

        def baseJiraUrl = 'https://projects.engineering.redhat.com'
        def apiUrl = baseJiraUrl + '/rest/api/2/issue/' + settings['issue'] + '/comment'
        def request = """\
      {
        "body": "${settings.comment}"
      }
      """
        echo "Request:\n${request}"

        httpRequest(authentication: 'jira', timeout: 30, contentType: 'APPLICATION_JSON', httpMode: 'POST', ignoreSslErrors: true, requestBody: request, url: apiUrl, validResponseCodes: '201')
    }

    @NonCPS
    def completeDeployment(Map settings) {
        def requiredFields = ['issue', 'result']
        def missingFields = []
        for (int i = 0; i < requiredFields.size(); i++) {
            def field = requiredFields[i]
            if (!settings.containsKey(field)) {
                missingFields.add(field)
            }
        }
        if (missingFields.size() > 0) {
            throw new java.security.InvalidParameterException("completeDeployment() missing field(s): " + missingFields.join(', '))
        }
        // build status of null means successful
        def buildStatus = settings['result'] ?: 'SUCCESSFUL'
        def comment = "Deployment has been successfully completed!"
        def status = "Fixed"

        if (buildStatus != 'SUCCESSFUL') {
            this.addIssueComment(issue: settings['issue'], comment: "Deployment failed! Please view ${env.BUILD_URL} for more information.")
            status = "Incomplete"
        }

        if (buildStatus == 'UNSTABLE') {
            comment = "Deploy has been completed as successful, however there were one or more non-fatal errors.  Please check ${env.BUILD_URL} for more information."
            status = "Incomplete"
        }

        def baseJiraUrl = 'https://projects.engineering.redhat.com'
        def apiUrl = baseJiraUrl + '/rest/api/2/issue/' + settings['issue'] + '/transitions'
        def request = """\
            {
              "update": {
                "comment": [
                  {
                    "add": {
                      "body": "${comment}"
                    }
                  }
                ]
              },
              "fields": {
                "assignee": {
                  "name": "cp-devops-jira"
                }
              },
              "transition": {
                "id": "51"
              }
            }
        """
        echo "Request:\n${request}"

        httpRequest(authentication: 'jira', timeout: 30, contentType: 'APPLICATION_JSON', httpMode: 'POST', ignoreSslErrors: true, requestBody: request, url: apiUrl, validResponseCodes: '204')
    }

}

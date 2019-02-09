# Jenkins Shared Libraries

This repository contains the shared Jenkins2 (Pipeline as Code) libraries. It has the following features:

* Build Integration - Maven, Gradle, Grunt, and NPM 
* Openshift Integration - Build & Deployment
* Build Notifications - Over Email, Slack, Hangouts, etc..
* Jira Integration
* GIT Integration
* Sonar Integration
* Docker Integration

## Installation and  Usage
In order to use it, you have to first import this library into the Jenkinsfile:

```
@Library('jenkins-shared-library') _
/* Using a version specifier, such as branch, tag, etc */
@Library('jenkins-shared-library@1.0') _
/* Accessing multiple libraries with one statement */
@Library(['jenkins-shared-library-1', 'jenkins-shared-library-2']) _
```
For more information on Global Shared Libraries, please follow this link - [jenkins-shared-library](https://jenkins.io/doc/book/pipeline/shared-libraries/)

## Reference Jenkinsfile
[Jenkinsfile](https://github.com/vijayg92/jenkins-shared-libraries/blob/master/Jenkinsfile) 

## Contributing
Pull requests are welcome.

## License
[GPLv3](https://en.wikipedia.org/wiki/GNU_General_Public_License) 

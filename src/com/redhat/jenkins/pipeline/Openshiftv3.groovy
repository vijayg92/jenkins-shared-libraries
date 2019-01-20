package com.redhat.jenkins.pipeline

import groovy.json.JsonSlurper

class Openshiftv3 {


        // Validate appconfig.yaml and fetch the application list
        def getApps(String filePath){
            try {
                app_list = [:]
                appconfig = readYaml file: (filePath)
                apps = appconfig['project']['apps']
                for (Map app : apps) {
                    keys = app.keySet()
                    for (String key : keys) {
                        app_list[key] = app.get(key).get('version')
                    }
                }
                return app_list
            } catch(FileNotFoundException e) {
                throw e
            }
        }

        // Get application version deployed on Openshift
        def getAppVersion(Map settings){
            def requiredFields = ['env', 'project', 'app']
            def missingFields = []
            requiredFields.each { field ->
                if (settings[field] == null) {
                    missingFields.add(field)
                }
            }
            if (missingFields.size() > 0) {
                throw new java.security.InvalidParameterException("getAppVersion() missing field(s): " + missingFields.join(', '))
            }
            def env = settings['env']
            def project = settings['project']
            def app = settings['app']

            try {
                openshift.withCluster(env) {
                    openshift.withCredentials("$project-$env") {
                        openshift.withProject(project) {
                            def dcSelector = openshift.selector('dc', "${app}").asJson()
                            def jsonParser = new JsonSlurper()
                            def object = jsonParser.parseText(dcSelector)
                            String image = object['spec']['template']['spec']['containers']['image']
                            def latest_app_tag = image.split(':')
                            return latest_app_tag[-1].replace(']','')
                        }
                    }
                }
            } catch(err) {
                throw err
            }
        }

        // Validate Application Tagging - Semantic Version Checks
        def boolean semVerValidation(Map settings) {
            def requiredFields = ['oldVersion', 'newVersion']
            def missingFields = []
            requiredFields.each { field ->
                if (settings[field] == null) {
                    missingFields.add(field)
                }
            }
            if (missingFields.size() > 0) {
                throw new java.security.InvalidParameterException("semVerValidation() missing field(s): " + missingFields.join(', '))
            }
            def newVer = settings['newVersion']
            def oldVer = settings['oldVersion']

            if(newVer == null || oldVer == null) {
                error ("Application version cannot be null!!")
            }

            if(!newVer.matches("[0-9]+\\.[0-9]+\\.[0-9]+") || !oldVer.matches("[0-9]+\\.[0-9]+\\.[0-9]+")) {
                error ("Application Version must follow Major,Minor Release format: [0-9]+.[0-9]+.[0-9]+")
            }

            String[] newVerParts = newVer.split("\\.",3);
            String[] oldVerParts = oldVer.split("\\.",3);

            for(int i = 0; i < 3; i++) {
                int newVerPart = i < newVerParts.length ?
                        Integer.parseInt(newVerParts[i]) : 0;
                int oldVerPart = i < oldVerParts.length ?
                        Integer.parseInt(oldVerParts[i]) : 0;
                if(newVerPart > oldVerPart) {
                    return true
                }
                if(newVerPart < oldVerPart) {
                    error "New Application Version is lesser than Old Version. Kindly bump up the Application Version";
                }
            }
            error "Both newVersion & deployedVersion are same.Kindly bump up Application Version!!"
        }

        // ImageValidation Checks - Defined by IT MPaaS Team
        def validateImage(Map settings){
            def requiredFields = ['app', 'tag']
            def missingFields = []
            requiredFields.each { field ->
                if (settings[field] == null) {
                    missingFields.add(field)
                }
            }
            if (missingFields.size() > 0) {
                throw new java.security.InvalidParameterException("validateImage() missing field(s): " + missingFields.join(', '))
            }
            def image = settings['app']
            def tag = settings['tag']
            // Need to add logic here to integrate ImageValidate checks
            return [image, tag]
        }

        // Openshift Build Application
        def buildApps(Map settings){
            def requiredFields = ['env', 'project', 'app', 'tag']
            def missingFields = []
            requiredFields.each { field ->
                if (settings[field] == null) {
                    missingFields.add(field)
                }
            }
            if (missingFields.size() > 0) {
                throw new java.security.InvalidParameterException("buildApps() missing field(s): " + missingFields.join(', '))
            }

            def env = settings['env']
            def project = settings['project']
            def app = settings['app']
            def tag = settings['tag']
            try {
                openshift.withCluster(env) {
                    openshift.withCredentials("$project-$env") {
                        openshift.withProject(project) {
                            def applied = openshift.process(
                                    '-f', "apps/${app}/build/build-${project}-${app}-template",
                                    // Configs & Secrets can be passed as a file reference too.
                                    //'--params-file', "apps/${app}/build/build-${project}-${app}-secrets",
                                    //'--params-file', "apps/${app}/build/build-${project}-${app}-configs",
                                    '-p', "project=${project}",
                                    '-p', "app=${app}",
                                    '-p', "tag=${tag}",
                            )
                            def result = openshift.apply(applied)
                            def build = result.narrow('bc').startBuild()
                            return build.logs('-f', '--pod-running-timeout=5m')
                        }
                    }
                }
            } catch(err) {
                throw err
            }
        }

        // Openshift Deploy Application
        def deployApps(Map settings){
            def requiredFields = ['env', 'project', 'app', 'tag']
            def missingFields = []
            requiredFields.each { field ->
                if (settings[field] == null) {
                    missingFields.add(field)
                }
            }
            if (missingFields.size() > 0) {
                throw new java.security.InvalidParameterException("deployApps() missing field(s): " + missingFields.join(', '))
            }
            def env = settings['env']
            def project = settings['project']
            def app = settings['app']
            def tag = settings['tag']

            try {
                openshift.withCluster(env) {
                    openshift.withCredentials("$project-$env") {
                        openshift.withProject(project) {
                            def processed = openshift.process(
                                    '-f', "apps/${app}/deploy/deploy-${project}-${app}-template",
                                    // Configs & Secrets can be passed as a file reference too.
                                    //'--params-file', "apps/${app}/build/build-${project}-${app}-secrets",
                                    //'--params-file', "apps/${app}/build/build-${project}-${app}-configs",
                                    '-p', "project=${project}",
                                    '-p', "app=${app}",
                                    '-p', "tag=${tag}"
                            )
                            openshift.apply(processed)
                            def dcSelector = openshift.selector( 'dc', [ app: app ] )
                            echo("Deploying dc=${dcSelector}")
                            dcSelector.rollout().latest()

                            def dc = openshift.selector( 'dc', [ app: app ] )
                            if (!dc.exists()) {
                                // dc.rollout().status() will not fail when no dc is found,
                                // so fail here
                                error "Could not find DeploymentConfig!"
                            }
                            return dc.rollout().status()
                        }
                    }
                }
            } catch(err) {
                throw err
            }
        }

        // Openshift RollBack - Openshift Prod Rollback
        def rollbackApps(Map settings){
            def requiredFields = ['env', 'project', 'app']
            def missingFields = []
            requiredFields.each { field ->
                if (settings[field] == null) {
                    missingFields.add(field)
                }
            }
            if (missingFields.size() > 0) {
                throw new java.security.InvalidParameterException("rollbackApps() missing field(s): " + missingFields.join(', '))
            }

            def env = settings['env']
            def project = settings['project']
            def app = settings['app']
            try {
                openshift.withCluster(env) {
                    openshift.withCredentials("$project-$env}") {
                        openshift.withProject(project) {
                            def dcSelector = openshift.selector( 'dc', [ app: app ] )
                            echo("Deploying dc=${dcSelector}")
                            dcSelector.rollout().undo("--to-revision=-1")
                            def dc = openshift.selector( 'dc', [ app: app ] )
                            if (!dc.exists()) {
                                error "Could not find DeploymentConfig!"
                            }
                            return dc.rollout().status()
                        }
                    }
                }
            } catch(err) {
                throw err
            }
        }
}

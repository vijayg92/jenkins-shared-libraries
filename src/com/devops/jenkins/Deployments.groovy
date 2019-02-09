package com.devops.jenkins

class Deployments implements Serializable {
    
    private String dev_kubectl_contex_name
    private String nonprod_kubelet_context_name
    private String deploy_release_name
    private String image_version
    private String dev_tiller_namespace
    private String non_prod_cluster_domain
    private String nonprod_tiller_name
    private String service_release_name
    private String nonprod_tiller_namespace


    static def deployOnDevCluster(script, dev_kubectl_contex_name, deploy_release_name, image_version, dev_tiller_namespace)     {
        try{
            def status = script.sh returnStdout: true, script: """#!/bin/bash
                                                            set -ex 
                                                            KUBECONFIG=/var/lib/jenkins/.kube/config-cijenkins-${dev_kubectl_contex_name} \
                                                            /usr/local/bin/helm upgrade --install --debug  ${deploy_release_name} \
                                                            --set service.version=${image_version} --set image.tag=${image_version} \
                                                            --tiller-namespace=${dev_tiller_namespace} --namespace=${dev_tiller_namespace} \
                                                            -f k8s/public-nonprod/helm-deployment.yaml rhapsody-helm/rhapsody-deployment"""
            sleep(time:75,unit:"SECONDS")
        } catch (err) {
            throw err
        }
    }

    static def deployOnPreProdCluster(script, nonprod_kubelet_context_name, service_release_name, nonprod_tiller_name, nonprod_tiller_namespace, non_prod_cluster_domain) {
        try{
            def status = script.sh returnStdout: true, script: """#!/bin/bash
            set -ex
            KUBECONFIG=/var/lib/jenkins/.kube/config-cijenkins-${nonprod_kubelet_context_name} \
            /usr/local/bin/helm upgrade --install --debug ${service_release_name} \
            --tiller-namespace=${nonprod_tiller_name} --namespace=${nonprod_tiller_namespace} \
            --set clusterDomain=${non_prod_cluster_domain} -f k8s/public-nonprod/helm-service.yaml rhapsody-helm/rhapsody-service"""
            sleep(time:75,unit:"SECONDS")
        } catch (err) {
            throw err
        } 
    }

    def deployOnProdCluster(){

    }
}
#!/bin/sh

#set up environment variables for templates to use
export APP_NAME=${APP_TARGET};
export APP_SPACE=${SPACE};
export APP_API_NAME="${APP_NAME}-${APP_SPACE}";
export APP_OWNER_CUID="hcapp";
export APP_OWNER_EMAIL="hcapp.app@centurylink.com";

export REG_BASE_URI="NE1ITCPRHAS62.ne1.savvis.net:4567";
export REG_PROJECT="/hyperconverse_dev/hyper-converse-fbmessage"
export REG_IMAGE="${REG_BASE_URI}${REG_PROJECT}:${VERSION}";
export REG_SECRET="reg-secret";

export K8S_CLUSTER_URL="https://kubeodc01-prod.corp.intranet:6443";
export K8S_NAMESPACE=${NAMESPACE};

#configure kubernetes access
#K8S_TOKEN should be set in gocd secret variables section
echo "*** setting up kubernetes access based on service account token ***";
kubectl config set-cluster mine --server=${K8S_CLUSTER_URL} --insecure-skip-tls-verify=true;
kubectl config set-credentials mine --token=${K8S_TOKEN};
kubectl config set-context mine --cluster=mine --user=mine --namespace=${K8S_NAMESPACE};
kubectl config use-context mine;
      
#kube secret to access docker registry 
#REG_TOKEN should be set in gocd secret variables section
echo "*** creating kubernetes secret to access the gitlab docker registry ***";
kubectl delete secret ${REG_SECRET} -n ${K8S_NAMESPACE} || true;
kubectl create secret docker-registry ${REG_SECRET} --docker-server=${REG_BASE_URI} --docker-username=gitlab-ci-token --docker-password=${REG_TOKEN} --docker-email=${APP_OWNER_EMAIL} -n ${K8S_NAMESPACE};
    
#kubernetes won't allow variables in the yaml files so using envsubst workaround so we can use them 
echo "*** creating deployment yaml files ***";
env envsubst < deployment.tmpl > deployment.yaml;
env envsubst < service.tmpl > service.yaml;
env envsubst < ingress.tmpl > ingress.yaml;

echo "*** deploying docker container and setting up the service and ingress  ***";
kubectl delete -f deployment.yaml;
kubectl create -f deployment.yaml
kubectl apply -f service.yaml -f ingress.yaml;

#show the ingress URL
echo "*** configured ingress is as follows. Add your context path for working URL";
echo "*** both http and https have been configured";
kubectl get ingress ${APP_API_NAME} -n ${K8S_NAMESPACE}



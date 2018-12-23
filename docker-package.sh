#!/bin/sh

# This script is used by GO CD and performs the following.
# - pulls jar and Dockerfile from Nexus
# - creates docker container and pushes to gitlab registry
#
# Variables not set here specifically are set in the GO CD environment
#

export REG_BASE_URI="NE1ITCPRHAS62.ne1.savvis.net:4567";
export REG_PROJECT="/hyperconverse_dev/hyper-converse-fbmessage"
export REG_IMAGE="${REG_BASE_URI}${REG_PROJECT}:${VERSION}";

#get the jar to be deployed.
#variable are set in GOCD pipeline environment.

echo "*** Getting jar from Nexus";
mkdir -p target;
curl -v -u ${NEXUS_USR}:${NEXUS_PWD} -o target/${TARGET}-${VERSION}.jar ${NEXUS_URL}/${TARGET}/${VERSION}/${TARGET}-${VERSION}.jar;

echo "*** Getting Dockerfile from Nexus";
curl -v -u ${NEXUS_USR}:${NEXUS_PWD} -o Dockerfile ${NEXUS_URL}/${TARGET}/${VERSION}/Dockerfile;

#build and push the docker image to your registry
#REG_TOKEN should be set in secret GOCD variable
echo "*** Building and pushing docker image to registry ${REG_BASE_URI}";
docker login -u gitlab-ci-token -p ${REG_TOKEN} ${REG_BASE_URI};
docker build -t ${REG_IMAGE} .;
docker push ${REG_IMAGE};
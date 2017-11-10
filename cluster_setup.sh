#!/bin/bash

echo "deb http://10.48.0.4/debian wheezy-backports main" > /etc/apt/sources.list.d/wzy-backports.list
echo "deb http://10.47.4.220:80/repos/infra-cli/3 /" > /etc/apt/sources.list.d/infra-cli-svc.list
sudo apt-get update
sudo apt-get install --yes --allow-unauthenticated infra-cli

function get_key() {
  echo "$1" | sed 's/,/\n/g' | grep $2 | awk 'BEGIN{FS=":"}{print $2}'
}

CONFIG=$(curl "http://10.47.0.101/v1/buckets/fk-sherlock-flash-install")
CONFIG=$(echo $CONFIG | tr -d {}\" | sed 's/keys://g' | sed 's/metadata://g')
PACKAGES=$(get_key $CONFIG packages)
FK_ENV=$(get_key $CONFIG fk-env)
PACKAGES=$(echo $PACKAGES | sed 's/___/ /g')
export MYSQL_HOSTS=$(get_key $CONFIG mysql-hosts)
MYSQL_HOST=$(python -c "import os;import random;hosts=os.environ.get('MYSQL_HOSTS').split('___');print hosts[random.randint(0,len(hosts)-1)]")
echo "$MYSQL_HOST   sherlock-app-slave-db.nm.flipkart.com" >> /etc/hosts

CLUSTER_NAME=$(get_key $CONFIG cluster-name)
AUTOSUGGEST_REPO_ENV=$(get_key $CONFIG sherlock-autosuggest-env)
reposervice --host repo-svc-app-0001.nm.flipkart.com --port 8080 env --name $AUTOSUGGEST_REPO_ENV --appkey dude > /etc/apt/sources.list.d/sherlock-flash.list

echo "$CLUSTER_NAME" > /etc/default/soa-cluster-name
echo "$FK_ENV" > /etc/default/fk-env

sudo apt-get update
for PACKAGE in $PACKAGES; do
  sudo apt-get install --yes --allow-unauthenticated $PACKAGE
done


#Jenkins Scripts

LAST_SUCCESSFUL_BUILD_VERSION=$(reposervice --host repo-svc-app-0001.nm.flipkart.com --port 8080 env --name "sherlock-autosuggest-env" --appkey dude | grep "fk-sherlock-flash" | awk '{print $2}' | awk 'BEGIN{FS="fk-sherlock-flash/"}{print$2}')
BUILD_VER_NUMBER=$(python -c "print $LAST_SUCCESSFUL_BUILD_VERSION + 1")
BUILD_VER_NUMBER="0.0.$BUILD_VER_NUMBER"

function die() {
    echo "ERROR: $1" >&2
    echo "Aborting." >&2
    exit 1
}

LOCAL_DIR=`pwd`
PACKAGE="fk-sherlock-flash"

[ -n "$LOCAL_DIR" ]	|| die "No base dir specified."
[ -n "$PACKAGE" ]	|| die "No package name specified."
[ -d "$LOCAL_DIR" ]	|| die "Base dir '$LOCAL_DIR' does not exist."

echo $JAVA_HOME

DEB_DIR="$LOCAL_DIR/deb-templates/$PACKAGE"
cp $LOCAL_DIR/target/$PACKAGE*.jar $DEB_DIR/usr/share/$PACKAGE/$PACKAGE.jar

echo "Updating CONTROL file ..."
sed -e "s/_VERSION_/${BUILD_VER_NUMBER}/" -i $DEB_DIR/DEBIAN/control
sed -e "s/_PACKAGE_/${PACKAGE}/" -i $DEB_DIR/DEBIAN/control
echo "Building deb file ${PACKAGE}_${BUILD_VER_NUMBER}.deb..."
DEBIAN_PACKAGE="$LOCAL_DIR/${PACKAGE}_${BUILD_VER_NUMBER}.deb"
dpkg-deb -b $DEB_DIR $DEBIAN_PACKAGE

echo $DEBIAN_PACKAGE
FLASH_REPO_VERSION=$(reposervice --host repo-svc-app-0001.nm.flipkart.com --port 8080 pubrepo --repo fk-sherlock-flash --appkey clientkey --debs $DEBIAN_PACKAGE | awk 'BEGIN{FS="fk-sherlock-flash/"}{print$2}')

REPO_PAYLOAD="[{\"repoName\":\"oracle-java\",\"repoReferenceType\":\"EXACT\",\"repoVersion\":8},{\"repoName\":\"fk-ops-tomcat8-base\",\"repoReferenceType\":\"EXACT\",\"repoVersion\":1},{\"repoName\":\"fk-ops-sgp-sherlock\",\"repoReferenceType\":\"EXACT\",\"repoVersion\":114},{\"repoName\":\"fk-sherlock-haproxy\",\"repoReferenceType\":\"EXACT\",\"repoVersion\":25},{\"repoName\":\"fk-sherlock-flash\",\"repoReferenceType\":\"EXACT\",\"repoVersion\":$FLASH_REPO_VERSION},{\"repoName\":\"logsvc\",\"repoReferenceType\":\"EXACT\",\"repoVersion\":301},{\"repoName\":\"fk-config-service-confd\",\"repoReferenceType\":\"EXACT\",\"repoVersion\":54},{\"repoName\":\"fk-ops-sgp-sherlock\",\"repoReferenceType\":\"EXACT\",\"repoVersion\":33},{\"repoName\":\"cosmos-v3\",\"repoReferenceType\":\"EXACT\",\"repoVersion\":22}]"

curl -X PUT -H "Content-Type: application/json" -d $REPO_PAYLOAD 'http://repo-svc-app-0001.nm.flipkart.com:8080/env/sherlock-autosuggest-env?appkey="12"'

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

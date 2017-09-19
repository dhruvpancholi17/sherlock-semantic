#!/bin/sh

QUERIES=$1
QUERIES=$(echo $QUERIES | sed "s/ /+/g")
USERNAME=monish.gandhi
PORT=25280
FILE_NAME=semanticBoxes.txt
#Fetch ips for semantic service from iaas API
curl -s "http://10.33.65.0:8080/compute/v1/apps/sherlock-app-cloud/instances?view=summary" | tr "," "\n" | grep -B6 "semantic-service" | grep primary | cut -d '"' -f4 >> $FILE_NAME
HOSTS_COUNT=$(wc -l < $FILE_NAME)
HOST_NUMBER=1

#Parallelising cache refresh
for QUERY in $(echo $QUERIES | sed "s/,/ /g") ; do
if [ $HOST_NUMBER -eq $HOSTS_COUNT ]; then
    HOST_NUMBER=1
fi
HOSTNAME=$(awk 'NR == n' n=$HOST_NUMBER $FILE_NAME)
echo $HOSTNAME
echo "Refreshing cache for $QUERY on $HOSTNAME"
 ssh -i keymo -oStrictHostKeyChecking=no -l ${USERNAME} ${HOSTNAME} "cd /var/lib/fk-w3-sherlock/semantic-cache-refresh/SemanticCacheRefreshUnix; nohup sudo sh semanticCacheUpdate.sh $QUERY >/dev/null 2>&1 &"
 HOST_NUMBER=$((HOST_NUMBER+1))
done

rm $FILE_NAME

echo "Cache refresh is running!!!"

exit 0
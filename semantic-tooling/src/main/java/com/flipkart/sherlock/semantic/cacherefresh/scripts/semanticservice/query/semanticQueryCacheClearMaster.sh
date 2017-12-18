#!/bin/sh

QUERIES=$1
QUERIES=$(echo $QUERIES | sed "s/ /+/g")
USERNAME=monish.gandhi
PORT=25280
FILE_NAME=semanticBoxes.txt

OUTPUT_LOG='/tmp/queryBasedRefresh_'$(date '+%d_%h_%Y_%H_%M_%S')

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
 ssh -i keymo -oStrictHostKeyChecking=no -l ${USERNAME} ${HOSTNAME} "cd /var/lib/fk-w3-sherlock/semantic-cache-refresh/SemanticCacheRefreshUnix; sudo sh semanticServiceCacheUpdateUnix.sh $QUERY " 2>$OUTPUT_LOG &
 HOST_NUMBER=$((HOST_NUMBER+1))
done

rm $FILE_NAME

echo "Cache clearing script is running. Waiting to finish all requests. Error logs in: $OUTPUT_LOG"
wait
echo "Clearing cache done. Please check error logs in $OUTPUT_LOG"

exit 0
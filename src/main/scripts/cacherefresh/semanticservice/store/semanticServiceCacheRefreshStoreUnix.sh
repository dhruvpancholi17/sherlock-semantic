#!/bin/sh

QUERIES=$1

FILENAME_PREFIX='cacheUpdateRequests_'$(date '+%d_%h_%Y_%H_%M_%S' | tr [:lower] [:upper])

FILE=$FILENAME_PREFIX'_StoreQueries.txt'
touch $FILE

USERNAME=monish.gandhi
PORT=25280
#Fetch ips for semantic service from iaas API
HOSTS=`curl -s "http://10.33.65.0:8080/compute/v1/apps/sherlock-app-cloud/instances?view=summary" | tr "," "\n" | grep -B6 "semantic-service" | grep primary | cut -d '"' -f4  | tr "\n" " ")`
CURRENT_DATE=$(date '+%d/%h/%Y' | tr [:lower] [:upper])
PREVIOUS_DATE=$(date --date="yesterday" '+%d/%h/%Y' | tr [:lower] [:upper])

echo "Aggregating logs for $PREVIOUS_DATE and $CURRENT_DATE in $FILE"
for QUERY in $(echo $QUERIES | sed "s/,/ /g") ; do

    echo "Store before preprocessing: $QUERY"
    FIRST_CHAR=`echo $QUERY | cut -c 1`

    #ensure store starts with /
    if [ $FIRST_CHAR != \/ ] ; then
        QUERY='/'$QUERY
    fi

    QUERY="$(echo $QUERY | sed 's/\//\\\//g')"  # escape /
    echo "Escaped store path: $QUERY";

    for HOSTNAME in ${HOSTS} ; do
      ssh -i keymo -oStrictHostKeyChecking=no -l ${USERNAME} ${HOSTNAME} "grep -e "$CURRENT_DATE" -e "$PREVIOUS_DATE" /var/log/flipkart/w3/sherlock/access.2017-*.log | grep '/semantic' | grep '$QUERY'|  cut -d' ' -f7" >> $FILE
    done

done

echo "Removing duplicate urls"
sort $FILE | uniq >> tmp && mv tmp $FILE
echo "Logs aggregation done"

LENGTH=$(wc -l < $FILE)
echo "Removing $LENGTH cache keys"
java -jar semantic-service-utils-1.0-SNAPSHOT.jar $FILE

rm $FILE
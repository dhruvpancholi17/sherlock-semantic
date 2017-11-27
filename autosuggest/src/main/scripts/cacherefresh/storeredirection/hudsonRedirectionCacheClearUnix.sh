#!/bin/sh

QUERIES=$1
QUERIES=$(echo $QUERIES | sed "s/ /+/g")    # replace space with +
USERNAME=monish.gandhi
PORT=25280

FILENAME_PREFIX='redirectionRequests_'$(date '+%d_%h_%Y_%H_%M_%S' | tr [:lower] [:upper])
FILE=$FILENAME_PREFIX'_queries.txt'
REFRESH_OUTPUT_FILE=$FILENAME_PREFIX'_cacheClearLog.txt'

touch $FILE

#Fetch ips for completions cluster from IAAS api
HOSTS=`curl -s "http://10.33.65.0:8080/compute/v1/apps/sherlock-app/instances?view=summary" | tr "," "\n" | grep -B6 "sherlock-completions-grp4" | grep primary | cut -d '"' -f4  | tr "\n" " ")`
CURRENT_DATE=$(date '+%Y-%m-%d')
PREVIOUS_DATE=$(date --date="yesterday" '+%Y-%m-%d')

#
echo "Aggregating logs for $PREVIOUS_DATE and $CURRENT_DATE in $FILE"
for QUERY in $(echo $QUERIES | sed "s/,/ /g") ; do
    echo "Fetching urls for query: $QUERY"
    for HOSTNAME in ${HOSTS} ; do
      ssh -i keymo -oStrictHostKeyChecking=no -l ${USERNAME} ${HOSTNAME} "grep '/redirection-store' /var/log/flipkart/w3/sherlock/access.$CURRENT_DATE.log /var/log/flipkart/w3/sherlock/access.$PREVIOUS_DATE.log | grep -ie '\&q='$QUERY'\&' -ie 'q='$QUERY'$' -ie '?q='$QUERY'&' |  cut -d' ' -f7 | sort | uniq "   >> $FILE
    done
done

sort $FILE | uniq >> redirectionTmp && mv redirectionTmp $FILE

LENGTH=$(wc -l < $FILE)
echo "Removing $LENGTH cache keys for $QUERIES"

#clear hudson cache by calling redirection api with x-cache get as false
while read line; do
    curl -X GET "http://10.47.1.159:25290$line" -H "content-type: application/json" -H "X-Cache-Get:false" -H "X-Flipkart-Client:mobile-apps" 1>/dev/null
done <$FILE

exit 0
#!/bin/bash -ex

echo "Starting build"

function die() {
    echo "ERROR: $1" >&2
    echo "Aborting." >&2
    exit 1
}

function cpp() {
	mkdir -p "${@:$#}"
	cp -rf "$@"
}

LOCAL_DIR=`pwd`
PACKAGE="fk-sherlock-flash"
BUILD_VER_NUMBER=$(head -1 $LOCAL_DIR/ChangeLog | awk 'BEGIN{FS=":"}{print $1}')


[ -n "$LOCAL_DIR" ]	|| die "No base dir specified."
[ -n "$PACKAGE" ]	|| die "No package name specified."
[ -d "$LOCAL_DIR" ]	|| die "Base dir '$LOCAL_DIR' does not exist."

JDK_DIRS="/usr/lib/jvm/java-8-oracle /usr/lib/jvm/j2sdk1.8-oracle" # /usr/lib/jvm/java-7-oracle /usr/lib/jvm/java-7-openjdk /usr/lib/jvm/java-7-openjdk-amd64/ /usr/lib/jvm/j2sdk1.7-oracle/"

# Look for the right JVM to use
for jdir in $JDK_DIRS; do
    if [ -r "$jdir/bin/java" -a -z "${JAVA_HOME}" ]; then
	JAVA_HOME="$jdir"
    fi
done
export JAVA_HOME
echo $JAVA_HOME

cd $LOCAL_DIR

DEB_DIR=$LOCAL_DIR/deb-templates/$PACKAGE

mvn -Dmaven.repo.local=/var/lib/semantic/.m2 clean install -DskipTests
mv $LOCAL_DIR/target/$PACKAGE*.jar $DEB_DIR/usr/share/$PACKAGE/$PACKAGE.jar

echo "Updating CONTROL file ..."
sed -e "s/_VERSION_/${BUILD_VER_NUMBER}/" -i $DEB_DIR/DEBIAN/control
sed -e "s/_PACKAGE_/${PACKAGE}/" -i $DEB_DIR/DEBIAN/control
echo "Building deb file ${PACKAGE}_${BUILD_VER_NUMBER}.deb..."
DEBIAN_PACKAGE="$LOCAL_DIR/${PACKAGE}_${BUILD_VER_NUMBER}.deb"
dpkg-deb -b $DEB_DIR $DEBIAN_PACKAGE

#"http://repo-svc-app-0001.nm.flipkart.com:8080/repo/fk-sherlock-flash/11"
UPLOAD_PATH=$(reposervice --host repo-svc-app-0001.nm.flipkart.com --port 8080 pubrepo --repo fk-sherlock-flash --appkey clientkey --debs $DEBIAN_PACKAGE)
echo $UPLOAD_PATH
dpkg -c $DEBIAN_PACKAGE

#UPLOAD_PATH=$(echo $UPLOAD_PATH)
#curl -X PUT -H "Content-Type: application/json" -d '[{"repoName":"oracle-java","repoReferenceType":"EXACT","repoVersion":8},{"repoName":"fk-ops-tomcat8-base","repoReferenceType":"EXACT","repoVersion":1},{"repoName":"fk-ops-sgp-sherlock","repoReferenceType":"EXACT","repoVersion":114},{"repoName":"fk-sherlock-haproxy","repoReferenceType":"EXACT","repoVersion":25},{"repoName":"fk-sherlock-flash","repoReferenceType":"EXACT","repoVersion":10},{"repoName":"logsvc","repoReferenceType":"EXACT","repoVersion":301},{"repoName":"fk-config-service-confd","repoReferenceType":"EXACT","repoVersion":54},{"repoName":"fk-ops-sgp-sherlock","repoReferenceType":"EXACT","repoVersion":33},{"repoName":"cosmos-v3","repoReferenceType":"EXACT","repoVersion":22}]' 'http://repo-svc-app-0001.nm.flipkart.com:8080/env/sherlock-autosuggest-env?appkey="12"'

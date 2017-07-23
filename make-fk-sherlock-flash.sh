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

reposervice --host repo-svc-app-0001.nm.flipkart.com --port 8080 pubrepo --repo fk-sherlock-flash --appkey clientkey --debs $DEBIAN_PACKAGE

dpkg -c $DEBIAN_PACKAGE

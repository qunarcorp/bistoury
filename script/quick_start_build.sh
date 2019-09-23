#!/bin/sh

cd "${0%/*}"

SCRIPT_DIR=`pwd`

cd ..


MVN_VERSION=`mvn -v 2>&1`
if [ $? -ne 0 ]; then
    echo "command mvn not found, Install the maven before executing the script！"
    exit 0;
fi

PROFILR='local'
JAVA_VERSION_GREATER_THAN_8_PROFILE="BigJavaVersion"
JAVA_VERSION_LESS_THAN_9_PROFILE="SmallJavaVersion"

if [[ `echo $MVN_VERSION | egrep "1\.[78]\."` ]]; then
    PROFILR="$PROFILR,$JAVA_VERSION_LESS_THAN_9_PROFILE"
else
    PROFILR="$PROFILR,$JAVA_VERSION_GREATER_THAN_8_PROFILE"
fi

BISTOURY_PROJECT_VERSION=`mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout`
BISTOURY_PACKAGE_FILE="bistoury-$BISTOURY_PROJECT_VERSION"
BISTOURY_PACKAGE_DIR="$SCRIPT_DIR/$BISTOURY_PACKAGE_FILE"

#打包agent
echo "================ starting to build bistoury agent ================"
mvn clean package -am -pl bistoury-dist -P$PROFILR -Dmaven.test.skip -Denforcer.skip=true
echo "================ building bistoury agent finished ================"

#打包ui
echo "================ starting to build bistoury ui    ================"
mvn clean package -am -pl bistoury-ui -P$PROFILR -Dmaven.test.skip=true -Denforcer.skip=true
echo "================ building bistoury ui finished    ================"

#打包proxy
echo "================ starting to build bistoury proxy ================"
mvn clean package -am -pl bistoury-proxy -P$PROFILR -Dmaven.test.skip=true -Denforcer.skip=true
echo "================ building bistoury proxy finished ================"

rm -rf "$BISTOURY_PACKAGE_DIR"

if [[ ! -w "$BISTOURY_PACKAGE_DIR" ]] ; then
mkdir -p "$BISTOURY_PACKAGE_DIR"
fi

mv bistoury-ui/target/bistoury-ui-bin $BISTOURY_PACKAGE_DIR
mv bistoury-proxy/target/bistoury-proxy-bin $BISTOURY_PACKAGE_DIR
mv bistoury-dist/target/bistoury-agent-bin $BISTOURY_PACKAGE_DIR
cp $SCRIPT_DIR/quick_start.sh $BISTOURY_PACKAGE_DIR
cp -R $SCRIPT_DIR/h2 $BISTOURY_PACKAGE_DIR

cd $SCRIPT_DIR
echo `pwd`
echo $BISTOURY_PACKAGE_FILE
tar -czvf $BISTOURY_PACKAGE_DIR"-quick-start.tar.gz" $BISTOURY_PACKAGE_FILE
rm -rf $BISTOURY_PACKAGE_FILE
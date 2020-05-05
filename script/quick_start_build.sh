#!/bin/bash

cd "${0%/*}"

SCRIPT_DIR=`pwd`

cd ..


PROFILR='local'

BISTOURY_PROJECT_VERSION=`./mvnw org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout`
BISTOURY_PACKAGE_FILE="bistoury-$BISTOURY_PROJECT_VERSION"
BISTOURY_PACKAGE_DIR="$SCRIPT_DIR/$BISTOURY_PACKAGE_FILE"

#打包agent
echo "================ starting to build bistoury agent ================"
./mvnw clean package -am -pl bistoury-dist -P$PROFILR -Dmaven.test.skip -Denforcer.skip=true
echo "================ building bistoury agent finished ================"

#打包ui
echo "================ starting to build bistoury ui    ================"
./mvnw clean package -am -pl bistoury-ui -P$PROFILR -Dmaven.test.skip=true -Denforcer.skip=true
echo "================ building bistoury ui finished    ================"

#打包proxy
echo "================ starting to build bistoury proxy ================"
./mvnw clean package -am -pl bistoury-proxy -P$PROFILR -Dmaven.test.skip=true -Denforcer.skip=true
echo "================ building bistoury proxy finished ================"

rm -rf "$BISTOURY_PACKAGE_DIR"

if [[ ! -w "$BISTOURY_PACKAGE_DIR" ]] ; then
mkdir -p "$BISTOURY_PACKAGE_DIR"
fi

mv bistoury-ui/target/bistoury-ui-bin $BISTOURY_PACKAGE_DIR/bistoury-ui
mv bistoury-proxy/target/bistoury-proxy-bin $BISTOURY_PACKAGE_DIR/bistoury-proxy
mv bistoury-dist/target/bistoury-agent-bin $BISTOURY_PACKAGE_DIR/bistoury-agent
cp $SCRIPT_DIR/quick_start.sh $BISTOURY_PACKAGE_DIR
cp -R $SCRIPT_DIR/h2 $BISTOURY_PACKAGE_DIR

cd $SCRIPT_DIR
echo `pwd`
echo $BISTOURY_PACKAGE_FILE
tar -czvf $BISTOURY_PACKAGE_DIR"-quick-start.tar.gz" $BISTOURY_PACKAGE_FILE
rm -rf $BISTOURY_PACKAGE_FILE
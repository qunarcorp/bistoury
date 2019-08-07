#!/usr/bin/env bash

cd "${0%/*}"

SCRIPT_DIR=`pwd`

cd ..

BISTOURY_PROJECT_VERSION=`mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout`

BISTOURY_PACKAGE_FILE=bistoury-$BISTOURY_PROJECT_VERSION
BISTOURY_PACKAGE_DIR="$SCRIPT_DIR/$BISTOURY_PACKAGE_FILE"
BISTOURY_MAEN_VERSION_DIR=$BISTOURY_PACKAGE_DIR/maven
BISTOURY_MAEN_VERSION_FILE=$BISTOURY_MAEN_VERSION_DIR/maven.version

#打包agent
echo "================ starting to build bistoury agent ================"
mvn clean package -am -pl bistoury-dist -Pbistoury-agent -Dmaven.test.skip -Denforcer.skip=true
echo "================ building bistoury agent finished ================"

#打包ui
echo "================ starting to build bistoury ui    ================"
mvn clean package -am -pl bistoury-ui -Plocal -Dmaven.test.skip=true -Denforcer.skip=true
echo "================ building bistoury ui finished    ================"

#打包proxy
echo "================ starting to build bistoury proxy ================"
mvn clean package -am -pl bistoury-proxy -Plocal -Dmaven.test.skip=true -Denforcer.skip=true
echo "================ building bistoury proxy finished ================"

rm -rf "$BISTOURY_PACKAGE_DIR"

if [[ ! -w "$BISTOURY_PACKAGE_DIR" ]] ; then
mkdir -p "$BISTOURY_PACKAGE_DIR"
fi

if [[ ! -w "$BISTOURY_MAEN_VERSION_DIR" ]] ; then
mkdir -p "$BISTOURY_MAEN_VERSION_DIR"
fi

echo -n $BISTOURY_PROJECT_VERSION > "$BISTOURY_MAEN_VERSION_FILE"

mv bistoury-ui/target/bistoury-ui-$BISTOURY_PROJECT_VERSION-bin $BISTOURY_PACKAGE_DIR
mv bistoury-proxy/target/bistoury-proxy-$BISTOURY_PROJECT_VERSION-bin $BISTOURY_PACKAGE_DIR
mv bistoury-dist/target/bistoury-agent-$BISTOURY_PROJECT_VERSION-bin $BISTOURY_PACKAGE_DIR
cp $SCRIPT_DIR/quick_start.sh $BISTOURY_PACKAGE_DIR
cp -R $SCRIPT_DIR/h2 $BISTOURY_PACKAGE_DIR

cd $SCRIPT_DIR
echo `pwd`
echo $BISTOURY_PACKAGE_FILE
tar -czvf $BISTOURY_PACKAGE_DIR"_quick_start.tar.gz" $BISTOURY_PACKAGE_FILE
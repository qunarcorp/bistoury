#!/bin/sh

cd "${0%/*}"
cd ..

MVN_VERSION=`mvn -v 2>&1`
if [ $? -ne 0 ]; then
    echo "command mvn not found, Install the maven before executing the script！"
    exit 0;
fi

PROFILR='prod'
JAVA_VERSION_GREATER_THAN_8_PROFILE="BigJavaVersion"
JAVA_VERSION_LESS_THAN_9_PROFILE="SmallJavaVersion"

if [[ `echo $MVN_VERSION | egrep "1\.[78]\."` ]]; then
    PROFILR="$PROFILR,$JAVA_VERSION_LESS_THAN_9_PROFILE"
else
    PROFILR="$PROFILR,$JAVA_VERSION_GREATER_THAN_8_PROFILE"
fi

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
#!/bin/bash

cd "${0%/*}"
cd ..

PROFILR='prod'

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
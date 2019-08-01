#!/usr/bin/env bash

cd "${0%/*}"
cd ..

#打包agent
echo "================ starting to build bistoury agent ================"
mvn clean package -am -pl bistoury-dist -Pbistoury-agent -Dmaven.test.skip -Denforcer.skip=true
echo "================ building bistoury agent finished ================"

#打包ui
echo "================ starting to build bistoury ui    ================"
mvn clean package -am -pl bistoury-ui -Pbistoury-ui -Dmaven.test.skip=true -Denforcer.skip=true
echo "================ building bistoury ui finished    ================"

#打包proxy
echo "================ starting to build bistoury proxy ================"
mvn clean package -am -pl bistoury-proxy -Pbistoury-proxy -Dmaven.test.skip=true -Denforcer.skip=true
echo "================ building bistoury proxy finished ================"
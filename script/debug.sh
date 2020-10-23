#!/bin/bash

cd "${0%/*}"
cd ..

PROFILR='prod'
./mvnw clean package install -am -pl bistoury-dist -P$PROFILR -Dmaven.test.skip -Denforcer.skip=true

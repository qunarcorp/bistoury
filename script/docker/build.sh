#!/bin/bash

MAVEN_PROFILE="prod"

echo "maven profile: "$MAVEN_PROFILE

cd "${0%/*}"

SCRIPT_DIR=`pwd`/..

cd ../..

mvn -v
if [ $? -ne 0 ]; then
    echo "command mvn not found, install the maven first！"
    exit 0;
fi

BISTOURY_PROJECT_VERSION=`mvn org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -Dexpression=project.version -q -DforceStdout`
BISTOURY_PACKAGE_FILE="bistoury-docker-$BISTOURY_PROJECT_VERSION"
BISTOURY_PACKAGE_DIR="$SCRIPT_DIR/$BISTOURY_PACKAGE_FILE"

#打包agent
echo "================ starting to build bistoury agent ================"
mvn clean package -am -pl bistoury-dist -P$MAVEN_PROFILE -Dmaven.test.skip -Denforcer.skip=true
echo "================ building bistoury agent finished ================"

#打包ui
echo "================ starting to build bistoury ui    ================"
mvn clean package -am -pl bistoury-ui -P$MAVEN_PROFILE -Dmaven.test.skip=true -Denforcer.skip=true
echo "================ building bistoury ui finished    ================"

#打包proxy
echo "================ starting to build bistoury proxy ================"
mvn clean package -am -pl bistoury-proxy -P$MAVEN_PROFILE -Dmaven.test.skip=true -Denforcer.skip=true
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


#docker相关
build_docker(){
    cp  script/docker/agent/Dockerfile   $BISTOURY_PACKAGE_DIR/bistoury-agent-bin
    cp  -r script/docker/agent/demo   $BISTOURY_PACKAGE_DIR/bistoury-agent-bin
    cp  script/docker/ui/*      $BISTOURY_PACKAGE_DIR/bistoury-ui-bin
    cp  script/docker/proxy/*   $BISTOURY_PACKAGE_DIR/bistoury-proxy-bin
    mkdir -p $BISTOURY_PACKAGE_DIR/mysql/ \
    &&cp  script/docker/mysql/*   $BISTOURY_PACKAGE_DIR/mysql/ \
    &&cp  bistoury-ui/sql/bistoury_init.sql $BISTOURY_PACKAGE_DIR/mysql/ \
    &&cat  script/docker/agent/demo/bistoury_demo_init.sql >> $BISTOURY_PACKAGE_DIR/mysql/bistoury_init.sql

    cp script/docker/agent/demo/demo_docker_start.sh $SCRIPT_DIR/docker

    cd $BISTOURY_PACKAGE_DIR/bistoury-agent-bin
    docker build -t bistoury-agent -t bistoury-agent:v$BISTOURY_PROJECT_VERSION .

    cd $BISTOURY_PACKAGE_DIR/bistoury-agent-bin/demo
    docker build -t bistoury-demo -t bistoury-demo:v$BISTOURY_PROJECT_VERSION .

    cd $BISTOURY_PACKAGE_DIR/bistoury-proxy-bin
    docker build -t bistoury-proxy -t bistoury-proxy:v$BISTOURY_PROJECT_VERSION .

    cd $BISTOURY_PACKAGE_DIR/bistoury-ui-bin
    docker build -t bistoury-ui -t bistoury-ui:v$BISTOURY_PROJECT_VERSION .

    cd $BISTOURY_PACKAGE_DIR/mysql
    docker build -t bistoury-db -t bistoury-db:v$BISTOURY_PROJECT_VERSION .

    rm -rf $BISTOURY_PACKAGE_DIR
}

build_docker
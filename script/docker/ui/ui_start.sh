#!/bin/bash

TEMP=`getopt -o : --long zk-address:,ui-java-home:,ui-jdbc-url: -- "$@"`

eval set -- "$TEMP"

while true; do
  case "$1" in
    --zk-address )
      ZK_ADDRESS="$2"; shift 2 ;;
    --ui-java-home )
      UI_JAVA_HOME="$2"; shift 2 ;;
    --ui-jdbc-url )
      UI_JDBC_URL="$2"; shift 2 ;;
    * ) break ;;
  esac
done

echo "zk address: "$ZK_ADDRESS
echo "ui java home: "$UI_JAVA_HOME
echo "ui mysql url" $UI_JDBC_URL

/home/q/bistoury/ui/bin/bistoury-ui.sh -j $UI_JAVA_HOME  -r $ZK_ADDRESS -d $UI_JDBC_URL -f start
#!/bin/bash
PARSE_AGENT_ID="false"
PROXY_JDBC_URL=""

TEMP=`getopt -o : --long real-ip:,zk-address:,proxy-java-home:,proxy-jdbc-url: -- "$@"`

eval set -- "$TEMP"

while true; do
  case "$1" in
    --real-ip )
      REAL_IP="$2"; shift 2 ;;
    --zk-address )
      ZK_ADDRESS="$2"; shift 2 ;;
    --proxy-java-home )
      PROXY_JAVA_HOME="$2"; shift 2 ;;
    --proxy-jdbc-url )
      PROXY_JDBC_URL="$2"; shift 2 ;;
    * ) break ;;
  esac
done

echo "real ip: "$REAL_IP
echo "zk address: "$ZK_ADDRESS
echo "proxy java home: "$PROXY_JAVA_HOME
echo "proxy jdbc url: "$PROXY_JDBC_URL

/home/q/bistoury/proxy/bin/bistoury-proxy.sh -j $PROXY_JAVA_HOME -i $REAL_IP -r $ZK_ADDRESS -d $PROXY_JDBC_URL -f start
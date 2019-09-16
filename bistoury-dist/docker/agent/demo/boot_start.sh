#!/bin/bash

BISTOURY_APP_LIB_CLASS="org.springframework.web.servlet.DispatcherServlet"

PROXY_HOST=""
AGENT_JAVA_HOME=""

TEMP=`getopt -o : --long proxy-host:,app-class:,agent-java-home: -- "$@"`

eval set -- "$TEMP"

while true; do
  case "$1" in
    --proxy-host )
      PROXY_HOST="$2"; shift 2 ;;
    --app-class )
      BISTOURY_APP_LIB_CLASS="$2"; shift 2 ;;
    --agent-java-home )
      AGENT_JAVA_HOME="$2"; shift 2 ;;
    * ) break ;;
  esac
done

echo "proxy host: "$PROXY_HOST
echo "app class: "$BISTOURY_APP_LIB_CLASS
echo "agent java home: "$AGENT_JAVA_HOME

mkdir -p  /home/q/www/logs
nohup $AGENT_JAVA_HOME/bin/java -jar /home/q/www/spring-mvc-boot-0.0.1-SNAPSHOT.jar >/home/q/www/logs/boot.log &

APP_PID=`$AGENT_JAVA_HOME/bin/jps -l|awk '{if($2!="sun.tools.jps.Jps"){print $1 ;{exit}} }'`

echo "app pid: "$APP_PID

/home/q/bistoury/agent/bin/bistoury-agent.sh -j $AGENT_JAVA_HOME -p $APP_PID -c $BISTOURY_APP_LIB_CLASS -s $PROXY_HOST start

tail -f /dev/null

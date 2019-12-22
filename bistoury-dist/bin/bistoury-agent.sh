#!/bin/bash
set -euo pipefail

BISTOURY_BIN="${BASH_SOURCE-$0}"
BISTOURY_BIN="$(dirname "$BISTOURY_BIN")"
BISTOURY_BIN_DIR="$(cd "$BISTOURY_BIN"; pwd)"
BISTOURY_MAIN="qunar.tc.bistoury.indpendent.agent.Main"

. "$BISTOURY_BIN_DIR/base.sh"
. "$BISTOURY_BIN_DIR/bistoury-agent-env.sh"

for CMD in "$@";do true; done

APP_PID=""
LOCAL_IP=""

while getopts p:i:j:c:h opt;do
    case $opt in
        p) APP_PID=$OPTARG;;
        i) LOCAL_IP=$OPTARG;;
        j) JAVA_HOME=$OPTARG;;
        c) BISTOURY_APP_LIB_CLASS=$OPTARG;;
        h|*) echo "-p    通过-p指定应用进程pid"
           echo "-i    通过-i参数指定本机ip"
           echo "-j    通过-j指定java home"
           echo "-c    通过-c指定应用依赖的jar包中的一个类（推荐使用公司内部中间件的jar包或Spring相关包中的类，如org.springframework.web.servlet.DispatcherServlet），agent通过该类获取应用jar包路径"
           echo "-h    通过-h查看命令帮助"
           exit 0
    esac
done

if [[ "$JAVA_HOME" != "" ]];then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA=java;
fi

if [[ ! -n $BISTOURY_APP_LIB_CLASS ]]; then
    echo "请通过-c参数指定应用依赖的jar包中的一个类（推荐使用公司内部中间件的jar包或Spring相关包中的类，如org.springframework.web.servlet.DispatcherServlet），agent通过该类获取应用jar包路径"
    exit 0
fi

if [[ -n $LOCAL_IP ]]; then
    JAVA_OPTS="$JAVA_OPTS -Dbistoury.local.host=$LOCAL_IP"
fi

if [[ -n $APP_PID ]]; then
    JAVA_OPTS="$JAVA_OPTS -Dbistoury.user.pid=$APP_PID"
fi

CLASSPATH="$CLASSPATH:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/sa-jdi.jar"
JAVA_OPTS="$JAVA_OPTS -Dbistoury.app.lib.class=$BISTOURY_APP_LIB_CLASS -Xmx80m -Xmn50m -XX:+UseParallelGC -XX:+UseParallelOldGC -XX:+UseCodeCacheFlushing -Xloggc:${BISTOURY_LOG_DIR}/bistoury-gc-${TIMESTAMP}.log -XX:+PrintGC -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${BISTOURY_LOG_DIR}"
BISTOURY_PID_FILE="$BISTOURY_PID_DIR/bistoury-agent.pid"
BISTOURY_DAEMON_OUT="$BISTOURY_LOG_DIR/bistoury-agent.out"


start(){
    echo "Start bistoury agent ..."
    if [[ -f "$BISTOURY_PID_FILE" ]]; then
      if kill -0 `cat "$BISTOURY_PID_FILE"` > /dev/null 2>&1; then
         echo already running as process `cat "$BISTOURY_PID_FILE"`.
         exit 0
      fi
    fi
    nohup "$JAVA" -cp "$CLASSPATH" ${JAVA_OPTS} ${BISTOURY_MAIN} > "$BISTOURY_DAEMON_OUT" 2>&1 < /dev/null &
    if [[ $? -eq 0 ]]
    then
      /bin/echo -n $! > "$BISTOURY_PID_FILE"
      if [[ $? -eq 0 ]];
      then
        sleep 1
        echo STARTED
      else
        echo FAILED TO WRITE PID
        exit 1
      fi
    else
      echo SERVER DID NOT START
      exit 1
    fi
}
stop(){
    echo "Stopping bistoury agent ... "
    if [[ ! -f "$BISTOURY_PID_FILE" ]]
    then
      echo "no bistoury agent to stop (could not find file $BISTOURY_PID_FILE)"
    else
      kill -9 $(cat "$BISTOURY_PID_FILE")
      rm "$BISTOURY_PID_FILE"
      echo "STOPPED"
    fi
}

case ${CMD} in
start)
    start
    ;;
stop)
    stop
    exit 0
    ;;
restart)
    stop
    start
    ;;
*)
    echo "Usage: $0 {start|restart|stop}" >&2
esac


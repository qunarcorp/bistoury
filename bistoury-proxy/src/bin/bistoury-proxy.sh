#!/bin/bash
set -euo pipefail
BISTOURY_BIN="${BASH_SOURCE-$0}"
BISTOURY_BIN="$(dirname "$BISTOURY_BIN")"
BISTOURY_BIN_DIR="$(cd "$BISTOURY_BIN"; pwd)"
BISTOURY_MAIN="qunar.tc.bistoury.proxy.container.Bootstrap"

. "$BISTOURY_BIN_DIR/base.sh"
. "$BISTOURY_BIN_DIR/bistoury-proxy-env.sh"

for CMD in "$@";do true; done

LOCAL_IP=""
while getopts j:i:h opt;do
    case $opt in
        j) JAVA_HOME=$OPTARG;;
        i) LOCAL_IP=$OPTARG;;
        h|*) echo "-j    通过-j指定java home"
           echo "-i    通过-i参数指定本机ip"
           echo "-h    通过-h查看命令帮助"
           exit 0
    esac
done

if [[ "$JAVA_HOME" != "" ]];then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA=java;
fi

if [[ -n $LOCAL_IP ]]; then
    JAVA_OPTS="$JAVA_OPTS -Dbistoury.local.host=$LOCAL_IP"
fi

CLASSPATH="$CLASSPATH:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/sa-jdi.jar"
JAVA_OPTS="$JAVA_OPTS -Xloggc:${BISTOURY_LOG_DIR}/bistoury-gc-${TIMESTAMP}.log -XX:+PrintGC -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${BISTOURY_LOG_DIR}"
BISTOURY_PID_FILE="$BISTOURY_PID_DIR/bistoury-proxy.pid"
BISTOURY_DAEMON_OUT="$BISTOURY_LOG_DIR/bistoury-proxy.out"

start(){
    echo "Start bistoury proxy ..."
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
    echo "Stopping bistoury proxy ... "
    if [[ ! -f "$BISTOURY_PID_FILE" ]]
    then
      echo "no bistoury proxy to stop (could not find file $BISTOURY_PID_FILE)"
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
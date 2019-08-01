#!/usr/bin/env bash
set -euo pipefail

BISTOURY_BIN="${BASH_SOURCE-$0}"
BISTOURY_BIN="$(dirname "$BISTOURY_BIN")"
BISTOURY_BIN_DIR="$(cd "$BISTOURY_BIN"; pwd)"
BISTOURY_MAIN="qunar.tc.bistoury.indpendent.agent.Main"

. "$BISTOURY_BIN_DIR/base.sh"
. "$BISTOURY_BIN_DIR/bistoury-agent-env.sh"

if [[ "$JAVA_HOME" != "" ]];then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA=java;
fi
CLASSPATH="$CLASSPATH:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/lib/sa-jdi.jar"
JAVA_OPTS="$JAVA_OPTS -Xloggc:${BISTOURY_LOG_DIR}/bistoury-gc-${TIMESTAMP}.log -XX:+PrintGC -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${BISTOURY_LOG_DIR}"
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

if [[ $# == 1 ]]; then
    CMD=${1:-}
elif [[ $1 == "-pid" && $# == 3 ]]; then
    JAVA_OPTS="$JAVA_OPTS -Dbistoury.user.pid=$2"
    CMD=${3:-}
else
    echo 命令格式错误
    exit 0;
fi

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


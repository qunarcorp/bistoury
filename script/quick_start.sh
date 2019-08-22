#!/bin/sh

BISTOURY_BASE_DIR=`pwd`

H2_DATABASE_DIR="$BISTOURY_BASE_DIR/h2"

APP_LOG_DIR="/tmp"

BISTOURY_UI_DIR="$BISTOURY_BASE_DIR/bistoury-ui-bin"
BISTOURY_UI_BIN_DIR="$BISTOURY_UI_DIR/bin"

BISTOURY_PROXY_DIR="$BISTOURY_BASE_DIR/bistoury-proxy-bin"
BISTOURY_PROXY_BIN_DIR="$BISTOURY_PROXY_DIR/bin"

BISTOURY_AGENT_DIR="$BISTOURY_BASE_DIR/bistoury-agent-bin"
BISTOURY_AGENT_BIN_DIR="$BISTOURY_AGENT_DIR/bin"
BISTOURY_AGENT_APP_LIB_CLASS="";

BISTOURY_PROXY_CONF_DIR="/tmp/bistoury"
BISTOURY_PROXY_CONF_FILE="$BISTOURY_PROXY_CONF_DIR/proxy.conf"

start(){

    cd $H2_DATABASE_DIR
    ./h2.sh -j $2 -l $APP_LOG_DIR start
    sleep 5

    cd $BISTOURY_PROXY_BIN_DIR
    ./bistoury-proxy.sh -j $2 start
    #等待proxy启动
    sleep 5

    cd $BISTOURY_AGENT_BIN_DIR
    if [[ -n $BISTOURY_AGENT_APP_LIB_CLASS ]]; then
        ./bistoury-agent.sh -p $1 -j $2 -c "$BISTOURY_AGENT_APP_LIB_CLASS" start
    else
        ./bistoury-agent.sh -p $1 -j $2 start
    fi

    cd $BISTOURY_UI_BIN_DIR
    ./bistoury-ui.sh -j $2 start

    cd $BISTOURY_BASE_DIR
}

stop(){

    cd $BISTOURY_UI_BIN_DIR
    ./bistoury-ui.sh stop

    cd $BISTOURY_AGENT_BIN_DIR
    ./bistoury-agent.sh stop

    cd $BISTOURY_PROXY_BIN_DIR
    ./bistoury-proxy.sh stop

    cd $H2_DATABASE_DIR
    ./h2.sh stop

    cd $BISTOURY_BASE_DIR
}

CMD=${!#}

while getopts p:j:l:c:h opt;do
    case $opt in
        p) APP_PID=$OPTARG;;
        j) JAVA_HOME=$OPTARG;;
        l) APP_LOG_DIR=$OPTARG;;
        c) BISTOURY_AGENT_APP_LIB_CLASS=$OPTARG;;
        h|*) echo "-p    通过-p指定应用进程pid"
           echo "-j    通过-j指定java home"
           echo "-l    通过-l参数指定应用日志目录，不指定时使用/tmp目录"
           echo "-c    通过-c指定应用依赖的jar包中的一个类（推荐使用公司内部中间件的jar包或Spring相关包中的类，如org.springframework.web.servlet.DispatcherServlet）"
           echo "-h    通过-h查看命令帮助"
           exit 0
    esac
done

if [[ ! -w "$BISTOURY_PROXY_CONF_DIR" ]] ; then
    mkdir -p "$BISTOURY_PROXY_CONF_DIR"
fi

if [[ "start" == $CMD ]] && [[ ! -n $JAVA_HOME ]]; then
    echo "请配置环境变量JAVA_HOME或通过-j参数指定JAVA_HOME"
    exit 0;
fi

if [[ "start" == $CMD ]] && [[ ! -n $BISTOURY_AGENT_APP_LIB_CLASS ]]; then
    echo "没有指定-c参数，agent将通过org.springframework.web.servlet.DispatcherServlet获取应用jar包路径"
fi

if [[ "start" == $CMD ]] && [[ -n "$APP_PID" &&  -n "$JAVA_HOME" ]]; then
    ln -s $JAVA_HOME /tmp/bistoury/java
    PROXY_TOMCAT_PORT=9090
    PROXY_WEBSOCKET_PORT=9881;
    echo "127.0.0.1:$PROXY_TOMCAT_PORT:$PROXY_WEBSOCKET_PORT">$BISTOURY_PROXY_CONF_FILE
    start $APP_PID $JAVA_HOME $BISTOURY_AGENT_APP_LIB_CLASS
elif [[ "stop" == $CMD ]]; then
    stop
    rm -rf $BISTOURY_PROXY_CONF_FILE
else
    echo "命令格式错误，Usage: [$0 -p pid -j java_home start] or [$0 stop]"
    exit 0
fi
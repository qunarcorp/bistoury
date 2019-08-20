#!/usr/bin/env bash

H2_DIR=`pwd`
H2_LOG_FILE=$H2_DIR/h2.log
H2_PID_FILE=$H2_DIR/h2.pid
H2_DATA_BASE_URL="/tmp/bistoury/h2/bistoury;MODE=MYSQL;TRACE_LEVEL_SYSTEM_OUT=2;AUTO_SERVER=TRUE;"
APP_LOG_DIR="\/tmp"
CMD=${!#}

while getopts j:l:h opt;do
    case $opt in
        j) JAVA_HOME=$OPTARG;;
        l) APP_LOG_DIR=$OPTARG;;
        h|*) echo "-j    通过-j指定java home"
           echo "-l    通过-l指定应用日志目录"
           echo "-h    通过-h查看命令帮助"
           exit 0
    esac
done

if [[ "$JAVA_HOME" != "" ]];then
    JAVA="$JAVA_HOME/bin/java"
else
    JAVA=java;
fi

start(){

    echo "Init tables"
    $JAVA -cp h2-1.4.199.jar org.h2.tools.RunScript -url "jdbc:h2:file:$H2_DATA_BASE_URL" -script ./schema.sql

    echo "Init datas"
    #替换数据库初始化文件中的sql
    local_ip=`/sbin/ifconfig -a|grep inet|grep -v 127.0.0.1|grep -v inet6|awk '{print $2}'|tr -d "addr:"|tail -1`
    local_host=`hostname`
    APP_LOG_DIR=` echo $APP_LOG_DIR | sed 's#\/#\\\/#g'`
    sed 's/${local_ip}/'$local_ip'/g' data.sql | sed 's/${local_host}/'$local_host'/g'|sed 's/${log_dir}/'$APP_LOG_DIR'/g' >newdata.sql

    $JAVA -cp h2-1.4.199.jar org.h2.tools.RunScript -url "jdbc:h2:file:$H2_DATA_BASE_URL" -script ./newdata.sql

    #rm -rf newdata.sql

    echo "Start h2 database"
    nohup $JAVA -cp h2*.jar org.h2.tools.Server -ifNotExists> "$H2_LOG_FILE" 2>&1 < /dev/null &

    if [[ $? -eq 0 ]]
    then
      /bin/echo -n $! > "$H2_PID_FILE"
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
    echo "Stopping h2 database ... "
    if [[ ! -f "$H2_PID_FILE" ]]
    then
      echo "no h2 database to stop (could not find file $H2_PID_FILE)"
    else
      kill -9 $(cat "$H2_PID_FILE")
      rm "$H2_PID_FILE"
      echo "STOPPED"
    fi
}
if [[ "start" == $CMD ]]; then
    start
elif [[ "stop" == $CMD ]]; then
    stop
else
    echo "命令格式错误，Usage: [$0 -j java_home start] or [$0 stop]"
    exit 0
fi
#!/bin/bash
# 当前qjdump 复制于vjdump version 1.0.7-SNAPSHOT

BISTOURY_BIN="${BASH_SOURCE-$0}"
BISTOURY_BIN="$(dirname "$BISTOURY_BIN")"
BISTOURY_BIN_DIR="$(cd "$BISTOURY_BIN"; pwd)"

. "$BISTOURY_BIN_DIR/qjtool-base.sh"

USAGE()
{
  echo "usage: qjdump [--liveheap][-nz|--nozip][-i|--interval] (pid:<PID>)"
}

if [ $# -lt 1 ]; then
  USAGE
  exit -1
fi

SLEEP_TIME=1
CLOSE_COMPRESS=0
NEED_HEAP_DUMP=0
PID="$1"

BASEDIR=/tmp/bistoury/qjtools/qjdump
LOGDIR=${BASEDIR}/${PID}

if [[ ! -w "$LOGDIR" ]] ; then
   mkdir -p "$LOGDIR"
fi

while true; do
  case "$1" in
    -i|--interval) SLEEP_TIME="$2"; PID="$3"; shift 1;;
    -nz|--nozip) CLOSE_COMPRESS=1; PID="$2"; shift;;
    --liveheap) NEED_HEAP_DUMP=1; PID="$2"; shift;;
    *) break;;
  esac
done

CMD="$1"
shift

START()
{
  if [[ x"$PID" == x ]]; then
     echo -e "The pid is empty, please enter pid".
     exit -1
  else
     echo -e "The pid is ${PID}"
  fi

  # clean all history logs
  rm -rf ${LOGDIR}/*.log ${LOGDIR}/*jmap_dump_live-*.bin
  mkdir -p ${LOGDIR}

  DATE=$(date "+%Y%m%d%H%M%S")

  echo -e "\033[34m$(date '+%Y-%m-%d %H:%M:%S') qjdump begin. command interval is ${SLEEP_TIME}s.\033[0m"

  # jstack
  echo -e "$(date '+%Y-%m-%d %H:%M:%S') Begin to process jstack."
  JSTACK_LOG=${LOGDIR}/jstack-${PID}-${DATE}.log
  ${JAVA_HOME}/bin/jstack -l $PID > ${JSTACK_LOG}
  if [[ $? != 0 ]]; then
    echo -e "\033[31mprocess jstack error.\033[0m"
  fi
  echo -e "$(date '+%Y-%m-%d %H:%M:%S') Finish to process jstack."
  sleep ${SLEEP_TIME}

  # no vjtop, use other replacement
  # jinfo -flags $PID
  echo -e "$(date '+%Y-%m-%d %H:%M:%S') Begin to process jinfo -flags."
  JINFO_FLAGS_LOG=${LOGDIR}/jinfo-flags-${PID}-${DATE}.log
  ${JAVA_HOME}/bin/jinfo -flags $PID 1>${JINFO_FLAGS_LOG} 2>&1
  if [[ $? != 0 ]]; then
    echo -e "\033[31mprocess jinfo -flags error.\033[0m"
  fi
  echo -e "$(date '+%Y-%m-%d %H:%M:%S') Finish to process jinfo -flags."

  if [[ `${JAVA_HOME}/bin/java -version 2>&1 | egrep "1\.[78]\."` ]]; then
    #jmap -heap
    echo -e "$(date '+%Y-%m-%d %H:%M:%S') Begin to process jmap -heap."
    JMAP_HEAP_LOG=${LOGDIR}/jmap_heap-${PID}-${DATE}.log
    ${JAVA_HOME}/bin/jmap -heap $PID > ${JMAP_HEAP_LOG}
    if [[ $? != 0 ]]; then
      echo -e "\033[31mprocess jmap -heap error.\033[0m"
    fi
    echo -e "$(date '+%Y-%m-%d %H:%M:%S') Finish to process jmap -heap."
  fi

  # jmap -histo
  echo -e "$(date '+%Y-%m-%d %H:%M:%S') Begin to process jmap -histo."
  JMAP_HISTO_LOG=${LOGDIR}/jmap_histo-${PID}-${DATE}.log
  ${JAVA_HOME}/bin/jmap -histo $PID > ${JMAP_HISTO_LOG}
  if [[ $? != 0 ]]; then
    echo -e "\033[31mprocess jmap -histo error.\033[0m"
  fi
  echo -e "$(date '+%Y-%m-%d %H:%M:%S') Finish to process jmap -histo."
  sleep ${SLEEP_TIME}

  # jmap -histo:live
  echo -e "$(date '+%Y-%m-%d %H:%M:%S') Begin to process jmap -histo:live."
  JMAP_HISTO_LIVE_LOG=${LOGDIR}/jmap_histo_live-${PID}-${DATE}.log
  ${JAVA_HOME}/bin/jmap -histo:live $PID > ${JMAP_HISTO_LIVE_LOG}
  if [[ $? != 0 ]]; then
    echo -e "\033[31mprocess jmap -histo:live error.\033[0m"
  fi
  echo -e "$(date '+%Y-%m-%d %H:%M:%S') Finish to process jmap -histo:live."
  sleep ${SLEEP_TIME}

  # jmap -dump:live
  if [[ $NEED_HEAP_DUMP == 1 ]]; then
    JMAP_DUMP_FILE=${LOGDIR}/jmap_dump_live-${PID}-${DATE}.hprof
    echo -e "$(date '+%Y-%m-%d %H:%M:%S') Begin to process jmap -dump:live."
    ${JAVA_HOME}/bin/jmap -dump:live,format=b,file=${JMAP_DUMP_FILE} $PID
    if [[ $? != 0 ]]; then
      echo -e "\033[31mprocess jmap -dump:live error.\033[0m"
    fi
    echo -e "$(date '+%Y-%m-%d %H:%M:%S') Finish to process jmap -dump:live."

    sleep ${SLEEP_TIME}
  fi

  # gc log
  echo -e "$(date '+%Y-%m-%d %H:%M:%S') Begin to process gc log."
  GCLOG=$(strings /proc/${PID}/cmdline |grep '\-Xloggc' |cut -d : -f 2)
  if [[ x"$GCLOG" == x ]]; then
    echo -e "No GC log existing."
  else
    # "\cp" means unalias cp, it can cover files without prompting
    \cp -rf $GCLOG ${LOGDIR}/gc-${PID}-${DATE}.log
    if [[ $? != 0 ]]; then
      echo -e "copy gc log error."
    fi
  fi
  echo -e "$(date '+%Y-%m-%d %H:%M:%S') Finish to process gc log."

  # packaging
  if [[ $CLOSE_COMPRESS == 1 ]]; then
    echo -e "The zip option is closed, no zip package will be generated."
  else
    echo -e "$(date '+%Y-%m-%d %H:%M:%S') Begin to zip all files."
    # zip files without heap dump
    ZIP_FILE=${BASEDIR}/qjdump-${PID}-${DATE}.zip
    zip -j ${ZIP_FILE} ${LOGDIR}/*.log
    if [[ $? != 0 ]]; then
      echo -e "\033[31mzip files error.\033[0m"
    else
      echo -e "zip files success, the zip file is \033[34m${ZIP_FILE}\033[0m"
    fi
    echo -e "$(date '+%Y-%m-%d %H:%M:%S') Finish to zip all files."
    
    if [[ $NEED_HEAP_DUMP == 1 ]]; then
      # compress all files
      echo -e "$(date '+%Y-%m-%d %H:%M:%S') Begin to zip files which include dump file."
      ZIP_FILE_WITH_HEAP_DUMP=${BASEDIR}/qjdump-with-heap-${PID}-${DATE}.zip
      zip -j ${ZIP_FILE_WITH_HEAP_DUMP} ${LOGDIR}/*.log ${JMAP_DUMP_FILE}
      if [[ $? != 0 ]]; then
        echo -e "\033[31mzip files which include dump file error.\033[0m"
      else
        echo -e "zip files which include dump file success, the zip path is \033[34m${ZIP_FILE_WITH_HEAP_DUMP}\033[0m"
      fi
      echo -e "$(date '+%Y-%m-%d %H:%M:%S') Finish to zip files which include dump file."
    fi
  fi
  echo -e "\033[34m$(date '+%Y-%m-%d %H:%M:%S') qjdump finish. \033[0m"
}

case "$CMD" in
  help) USAGE;;
  *) START;;
esac

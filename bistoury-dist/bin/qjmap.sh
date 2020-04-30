#!/bin/bash

BISTOURY_BIN="${BASH_SOURCE-$0}"
BISTOURY_BIN="$(dirname "$BISTOURY_BIN")"
BISTOURY_BIN_DIR="$(cd "$BISTOURY_BIN"; pwd)"

. "$BISTOURY_BIN_DIR/qjtool-base.sh"

SAJDI_PATH=$JAVA_HOME/lib/sa-jdi.jar
QJMAP_VERSION=`ls $BISTOURY_LIB_DIR|grep bistoury-commands`
VJMAP_VERSION=`ls $BISTOURY_LIB_DIR|grep vjmap`

VJMAP_PATH=$BISTOURY_LIB_DIR/$VJMAP_VERSION
QJMAP_PATH=$BISTOURY_LIB_DIR/$QJMAP_VERSION
TOOLS_PATH=$JAVA_HOME/lib/tools.jar

if [ ! -f "$SAJDI_PATH" ] ; then
	echo "$SAJDI_PATH doesn't exist !" >&2
	exit 1
fi


if [ ! -f "$TOOLS_PATH" ] ; then
	echo "$TOOLS_PATH doesn't exist !" >&2
	exit 1
fi

echo  "\033[31mWARNING!! STW(Stop-The-World) will be performed on your Java process, if this is NOT wanted, type 'Ctrl+C' to exit. \033[0m"


JAVA_OPTS="-Xms512m -Xmx512m -Xmn400m -XX:+UseConcMarkSweepGC -XX:+TieredCompilation -Xverify:none -XX:AutoBoxCacheMax=20000"

exec "$JAVA_HOME"/bin/java $JAVA_OPTS -classpath $VJMAP_PATH:$SAJDI_PATH:$TOOLS_PATH:$QJMAP_PATH qunar.tc.bistoury.commands.qjtools.QJMap $*
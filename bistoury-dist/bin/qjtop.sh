#!/bin/bash

BISTOURY_BIN="${BASH_SOURCE-$0}"
BISTOURY_BIN="$(dirname "$BISTOURY_BIN")"
BISTOURY_BIN_DIR="$(cd "$BISTOURY_BIN"; pwd)"

. "$BISTOURY_BIN_DIR/qjtool-base.sh"


VJTOP_VERSION=`ls $BISTOURY_LIB_DIR|grep vjtop`
QJTOP_VERSION=`ls $BISTOURY_LIB_DIR|grep bistoury-commands`


VJTOP_PATH=$BISTOURY_LIB_DIR/$VJTOP_VERSION
QJTOP_PATH=$BISTOURY_LIB_DIR/$QJTOP_VERSION
TOOLS_PATH=$JAVA_HOME/lib/tools.jar

if [ ! -f "$TOOLS_PATH" ] ; then
	echo "$TOOLS_PATH doesn't exist !" >&2
	exit 1
fi

JAVA_OPTS="-Xms256m -Xmx256m -XX:NewRatio=1 -Xss256k -XX:+UseSerialGC -XX:CICompilerCount=2 -Xverify:none -XX:AutoBoxCacheMax=20000"

exec "$JAVA_HOME"/bin/java $JAVA_OPTS -cp $VJTOP_PATH:$TOOLS_PATH:$QJTOP_PATH qunar.tc.bistoury.commands.qjtools.QJTop "$@"



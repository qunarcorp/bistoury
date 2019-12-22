#!/bin/bash

BISTOURY_BIN="${BASH_SOURCE-$0}"
BISTOURY_BIN="$(dirname "$BISTOURY_BIN")"
BISTOURY_BIN_DIR="$(cd "$BISTOURY_BIN"; pwd)"

. "$BISTOURY_BIN_DIR/qjtool-base.sh"

SAJDI_PATH=$JAVA_HOME/lib/sa-jdi.jar

VJMXCLI_VERSION=`ls $BISTOURY_LIB_DIR|grep bistoury-commands`
QJMXCLI_VERSION=`ls $BISTOURY_LIB_DIR|grep vjmxcli`

VJMXCLI_PATH=$BISTOURY_LIB_DIR/$VJMXCLI_VERSION
QJMXCLI_PATH=$BISTOURY_LIB_DIR/$QJMXCLI_VERSION

TOOLS_PATH=$JAVA_HOME/lib/tools.jar



if [ ! -d "$JAVA_HOME" ] ; then
	echo "Please set JAVA_HOME env before run this script"
	exit 1
fi


if [ ! -f "$TOOLS_PATH" ] ; then
	echo "$TOOLS_PATH doesn't exist !" >&2
	exit 1
fi


JAVA_OPTS="-Xms96m -Xmx96m -Xmn64m -Xss256k -XX:+UseSerialGC -Djava.compiler=NONE -Xverify:none -XX:AutoBoxCacheMax=20000" 

exec "$JAVA_HOME"/bin/java $JAVA_OPTS -cp $VJMXCLI_PATH:$QJMXCLI_PATH:$TOOLS_PATH qunar.tc.bistoury.commands.qjtools.QJMXClient $*
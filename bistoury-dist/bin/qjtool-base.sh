#!/bin/bash

. "$BISTOURY_BIN_DIR/base.sh"
. "$BISTOURY_BIN_DIR/bistoury-agent-env.sh"


if [[ "$JAVA_HOME" != "" ]];then
    JAVA_HOME="$JAVA_HOME"
else
    echo "Please set JAVA_HOME env before run this script"
    exit 1
fi


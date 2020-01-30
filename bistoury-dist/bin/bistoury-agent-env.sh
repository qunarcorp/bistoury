#!/bin/bash
set -euo pipefail
set +o nounset
BISTOURY_STORY_PATH="$BISTOURY_STORE_DIR"
test -z "$BISTOURY_PROXY_HOST" && BISTOURY_PROXY_HOST="127.0.0.1:9090"
test -z "$BISTOURY_APP_LIB_CLASS" && BISTOURY_APP_LIB_CLASS="org.springframework.web.servlet.DispatcherServlet"
test -z "$JAVA_HOME" && JAVA_HOME="/tmp/bistoury/java"
JAVA_OPTS="-Dbistoury.store.path=$BISTOURY_STORY_PATH -Dbistoury.proxy.host=$BISTOURY_PROXY_HOST"

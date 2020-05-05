#!/bin/bash
set -euo pipefail

BISTOURY_STORY_PATH="$BISTOURY_STORE_DIR"
BISTOURY_PROXY_HOST="127.0.0.1:9090"
BISTOURY_APP_LIB_CLASS="org.springframework.web.servlet.DispatcherServlet"
JAVA_HOME="/tmp/bistoury/java"
JAVA_OPTS="-Dbistoury.store.path=$BISTOURY_STORY_PATH -Dbistoury.proxy.host=$BISTOURY_PROXY_HOST"

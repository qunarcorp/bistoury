#!/usr/bin/env bash
set -euo pipefail

BISTOURY_STORY_PATH="/tmp/bistoury/store"
BISTOURY_PROXY_HOST="127.0.0.1:8080"
BISTOURY_APP_LIB_CLASS="org.springframework.web.servlet.DispatcherServlet"
JAVA_HOME="/tmp/java"
JAVA_OPTS="-Dbistoury.store.path=$BISTOURY_STORY_PATH -Dbistoury.proxy.host=$BISTOURY_PROXY_HOST -Dbistoury.app.lib.class=$BISTOURY_APP_LIB_CLASS"

#!/usr/bin/env bash
set -euo pipefail

BISTOURY_STORY_PATH="~/workspace/q/bistoury/store"
BISTOURY_PROXY_HOST="127.0.0.1:8080"
BISTOURY_USER_CLAZZ="org.springframework.web.servlet.DispatcherServlet"
JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk1.8.0_211.jdk/Contents/Home"
JAVA_OPTS="-Dbistoury.store.path=$BISTOURY_STORY_PATH -Dbistoury.proxy.host=$BISTOURY_PROXY_HOST -Dbistoury.app.lib.class=$BISTOURY_USER_CLAZZ"

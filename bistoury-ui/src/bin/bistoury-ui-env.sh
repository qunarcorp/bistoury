#!/bin/bash
set -euo pipefail
set +o nounset
test -z "$JAVA_HOME" && JAVA_HOME="/tmp/bistoury/java"
JAVA_OPTS="$JAVA_OPTS -Dbistoury.conf=$BISTOURY_COF_DIR -Dbistoury.cache=$BISTOURY_CACHE_DIR"

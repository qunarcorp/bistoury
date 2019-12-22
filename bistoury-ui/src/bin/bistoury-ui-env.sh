#!/bin/bash
set -euo pipefail

JAVA_HOME="/tmp/bistoury/java"
JAVA_OPTS="-Dbistoury.conf=$BISTOURY_COF_DIR -Dbistoury.cache=$BISTOURY_CACHE_DIR"

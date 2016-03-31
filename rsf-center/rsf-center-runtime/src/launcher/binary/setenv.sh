#!/bin/sh
# ----------------------------------------------------------------------------

#config
APP_CONFIG="$APP_HOME/conf/rsf-config.xml"

#console out
CONSOLE_OUT="$APP_HOME/logs/console.out"

#JPDA
JPDA_TRANSPORT="dt_socket"
JPDA_ADDRESS="8000"
JPDA_SUSPEND="n"

JAVA_OPTS="${JAVA_OPTS} -agentlib:jdwp=transport=$JPDA_TRANSPORT,address=$JPDA_ADDRESS,server=y,suspend=$JPDA_SUSPEND"
#!/bin/sh
# ----------------------------------------------------------------------------

#config
APP_CONFIG="$APP_HOME/conf/rsf-config.xml"

#console out
LOGS="$APP_HOME/logs"
CONSOLE_OUT="$LOGS/console.out"

#JPDA
JPDA_ENABLE="jpda"
JPDA_TRANSPORT="dt_socket"
JPDA_ADDRESS="8000"
JPDA_SUSPEND="n"

# JVM opts
JAVA_OPTS="-server"
JAVA_OPTS="${JAVA_OPTS} -Xms1024m -Xmx1024m -Xmn512m"
JAVA_OPTS="${JAVA_OPTS} -XX:SurvivorRatio=10"
JAVA_OPTS="${JAVA_OPTS} -XX:PermSize=16m -XX:MaxPermSize=64m"
JAVA_OPTS="${JAVA_OPTS} -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSMaxAbortablePrecleanTime=5000"
JAVA_OPTS="${JAVA_OPTS} -XX:+CMSClassUnloadingEnabled -XX:CMSInitiatingOccupancyFraction=80 -XX:+UseCMSInitiatingOccupancyOnly"
JAVA_OPTS="${JAVA_OPTS} -Dsun.rmi.dgc.server.gcInterval=2592000000 -Dsun.rmi.dgc.client.gcInterval=2592000000"
JAVA_OPTS="${JAVA_OPTS} -Xloggc:${LOGS}/gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps"
JAVA_OPTS="${JAVA_OPTS} -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOGS}/java.hprof"
JAVA_OPTS="${JAVA_OPTS} -Djava.awt.headless=true"
JAVA_OPTS="${JAVA_OPTS} -Dsun.net.client.defaultConnectTimeout=10000"
JAVA_OPTS="${JAVA_OPTS} -Dsun.net.client.defaultReadTimeout=30000"
JAVA_OPTS="${JAVA_OPTS} -DJM.LOG.PATH=${LOGS}"
JAVA_OPTS="${JAVA_OPTS} -Dfile.encoding=UTF-8"

# SETENV NEW OPTS
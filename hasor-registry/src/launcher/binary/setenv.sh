#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# -----------------------------------------------------------------------------

#Center Config
APP_CONFIG="$APP_HOME/conf/rsf-config.xml"

#Console out
LOGS="$APP_HOME/logs"
CONSOLE_OUT="$LOGS/console.out"

#JPDA
#JPDA_ENABLE="jpda"
JPDA_TRANSPORT="dt_socket"
JPDA_ADDRESS="8000"
JPDA_SUSPEND="n"

# JVM opts
JAVA_OPTS="-server"
JAVA_OPTS="${JAVA_OPTS} -noverify"
JAVA_OPTS="${JAVA_OPTS} -Xloggc:${LOGS}/gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps"
JAVA_OPTS="${JAVA_OPTS} -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOGS}/java.hprof"
JAVA_OPTS="${JAVA_OPTS} -Djava.awt.headless=true"
JAVA_OPTS="${JAVA_OPTS} -Dsun.net.client.defaultConnectTimeout=10000"
JAVA_OPTS="${JAVA_OPTS} -Dsun.net.client.defaultReadTimeout=30000"
JAVA_OPTS="${JAVA_OPTS} -DJM.LOG.PATH=${LOGS}"
JAVA_OPTS="${JAVA_OPTS} -Dfile.encoding=UTF-8"

# SETENV NEW OPTS

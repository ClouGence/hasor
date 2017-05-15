@echo off
rem ----------------------------------------------------------------------------
rem Licensed to the Apache Software Foundation (ASF) under one
rem or more contributor license agreements.  See the NOTICE file
rem distributed with this work for additional information
rem regarding copyright ownership.  The ASF licenses this file
rem to you under the Apache License, Version 2.0 (the
rem "License"); you may not use this file except in compliance
rem with the License.  You may obtain a copy of the License at
rem
rem    http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing,
rem software distributed under the License is distributed on an
rem "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
rem KIND, either express or implied.  See the License for the
rem specific language governing permissions and limitations
rem under the License.
rem -----------------------------------------------------------------------------

rem Center Config
set APP_CONFIG=%APP_HOME%\conf\rsf-config.xml

rem Console out
set LOGS=%APP_HOME%\logs
set CONSOLE_OUT=%LOGS%\console.out

rem JPDA
rem set JPDA_ENABLE=jpda
set JPDA_TRANSPORT=dt_socket
set JPDA_ADDRESS=8000
set JPDA_SUSPEND=n

rem JVM opts
set JAVA_OPTS=-server
set JAVA_OPTS=%JAVA_OPTS% -Xloggc:"%LOGS%\gc.log" -XX:+PrintGCDetails -XX:+PrintGCDateStamps
set JAVA_OPTS=%JAVA_OPTS% -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath="%LOGS%\java.hprof"
set JAVA_OPTS=%JAVA_OPTS% -Djava.awt.headless=true
set JAVA_OPTS=%JAVA_OPTS% -Dsun.net.client.defaultConnectTimeout=10000
set JAVA_OPTS=%JAVA_OPTS% -Dsun.net.client.defaultReadTimeout=30000
set JAVA_OPTS=%JAVA_OPTS% -DJM.LOG.PATH="%LOGS%"
set JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8

rem SETENV NEW OPTS
rem --------------------------------




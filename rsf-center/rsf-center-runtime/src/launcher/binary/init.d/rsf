#!/bin/sh
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

### BEGIN INIT INFO
# Provides:          rsf
# Required-Start:    $remote_fs $syslog
# Required-Stop:     $remote_fs $syslog
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Description:       Controls Apache Solr as a Service
### END INIT INFO

# Example of a very simple *nix init script that delegates commands to the bin/catalina.sh script
# Typical usage is to do:
#
#   cp bin/init.d/rsf /etc/init.d/rsf
#   chmod 755 /etc/init.d/rsf
#   chown root:root /etc/init.d/rsf
#   update-rc.d rsf defaults
#   update-rc.d rsf enable

RSF_HOME="/opt/rsf-center"

if [ ! -d "$RSF_HOME" ]; then
  echo "$RSF_HOME not found! Please check the RSF_HOME setting in your $0 script."
  exit 1
fi

case "$1" in
  start|stop)
    RSF_CMD="$1"
    ;;
  *)
    echo "Usage: $0 {start|stop}"
    exit
esac

su -c "RSF_HOME=\"$RSF_HOME/bin/catalina.sh\" "$RSF_CMD" -f

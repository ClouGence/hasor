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
#
# Control Script for the RsfCenter Server
#
# Required ENV vars:
# ------------------
#   APP_HOME   - location of app's installed home dir
#   JAVA_HOME - location of a JDK home dir
#   JAVA_OPTS - parameters passed to the Java VM when running app
#     e.g. to debug App itself, use
#       set APP_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
# -----------------------------------------------------------------------------
#
#
# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
darwin=false
mingw=false

case "`uname`" in
  CYGWIN*) cygwin=true ;;
  MINGW*) mingw=true;;
  Darwin*) darwin=true;;
esac

# resolve links - $0 may be a softlink
PRG="$0"
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set APP_HOME if not already set
[ -z "$APP_HOME" ] && APP_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`

if [ -r "$APP_HOME/bin/setenv.sh" ]; then
  . "$APP_HOME/bin/setenv.sh"
fi

if [ "$JPDA_ENABLE" = "jpda" ] ; then
  if [ -z "$JPDA_TRANSPORT" ]; then
    JPDA_TRANSPORT="dt_socket"
  fi
  if [ -z "$JPDA_ADDRESS" ]; then
    JPDA_ADDRESS="8000"
  fi
  if [ -z "$JPDA_SUSPEND" ]; then
    JPDA_SUSPEND="n"
  fi
  if [ -z "$JPDA_OPTS" ]; then
    JPDA_OPTS="-agentlib:jdwp=transport=$JPDA_TRANSPORT,address=$JPDA_ADDRESS,server=y,suspend=$JPDA_SUSPEND"
  fi
fi


# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  APP_HOME=`cygpath --absolute --windows "$APP_HOME"`
fi

if $darwin; then
  # Look for the Apple JDKs first to preserve the existing behaviour, and then look for the new JDKs provided by Oracle.
  if [[ -z "$JAVA_HOME" && -L /System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK ]] ; then
      export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Home
  fi
  if [[ -z "$JAVA_HOME" && -L /System/Library/Java/JavaVirtualMachines/CurrentJDK ]] ; then
    export JAVA_HOME=/System/Library/Java/JavaVirtualMachines/CurrentJDK/Contents/Home
  fi
  if [[ -z "$JAVA_HOME" && -L "/Library/Java/JavaVirtualMachines/CurrentJDK" ]] ; then
    export JAVA_HOME=/Library/Java/JavaVirtualMachines/CurrentJDK/Contents/Home
  fi
  if [[ -z "$JAVA_HOME" && -x "/usr/libexec/java_home" ]] ; then
    export JAVA_HOME=/usr/libexec/java_home
  fi
fi

if [ -z "$JAVA_CMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVA_CMD="$JAVA_HOME/jre/sh/java"
    else
      JAVA_CMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVA_CMD="`which java`"
  fi
fi

if [ ! -x "$JAVA_CMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly." >&2
  echo "  We cannot execute $JAVA_CMD" >&2
  exit 1
fi

if [ -z "$APP_PID" ]; then
  APP_PID="rsf-center.pid"
fi

# Bugzilla 37848: When no TTY is available, don't output to console
have_tty=0
if [ "`tty`" != "not a tty" ]; then
    have_tty=1
fi

# Bugzilla 37848: only output this if we have a TTY
if [ $have_tty -eq 1 ]; then
  echo "Using JAVA_HOME  : $JAVA_HOME"
  echo "Using APP_HOME   : $APP_HOME"
  if [ ! -z "$APP_PID" ]; then
    echo "Using APP_PID    : $APP_PID"
  fi
  echo "Using CONSOLE_OUT: $CONSOLE_OUT"
fi

cd $APP_HOME
#
#
echo "--------------------------"

# ----- Execute The Requested Command -----------------------------------------
#
# check and clear $APP_PID file. (copy from catalina.sh)
check_app_pid() {
  if [ ! -z "$APP_PID" ]; then
    if [ -f "$APP_PID" ]; then
      if [ -s "$APP_PID" ]; then
        echo "Existing PID file found during start."
        if [ -r "$APP_PID" ]; then
          PID=`cat "$APP_PID"`
          ps -p $PID >/dev/null 2>&1
          if [ $? -eq 0 ] ; then
            echo "RsfCenter appears to still be running with PID $PID. Start aborted."
            exit 1
          else
            echo "Removing/clearing stale PID file."
            rm -f "$APP_PID" >/dev/null 2>&1
            if [ $? != 0 ]; then
              if [ -w "$APP_PID" ]; then
                cat /dev/null > "$APP_PID"
              else
                echo "Unable to remove or clear stale PID file. Start aborted."
                exit 1
              fi
            fi
          fi
        else
          echo "Unable to read PID file. Start aborted."
          exit 1
        fi
      else
        rm -f "$APP_PID" >/dev/null 2>&1
        if [ $? != 0 ]; then
          if [ ! -w "$APP_PID" ]; then
            echo "Unable to remove or write to empty PID file. Start aborted."
            exit 1
          fi
        fi
      fi
    fi
  fi
}

#print rsfcenter version
do_version() {
    exec "$JAVA_CMD" -classpath "${APP_HOME}"/boot/plexus-classworlds-*.jar \
      "-Dclassworlds.conf=${APP_HOME}/bin/app.conf" \
      "-Dapp.home=${APP_HOME}"  \
      org.codehaus.plexus.classworlds.launcher.Launcher version "$@"
}

#start rsfCenter
do_exec() {
    exec "$JAVA_CMD" $JAVA_OPTS $JPDA_OPTS -classpath "${APP_HOME}"/boot/plexus-classworlds-*.jar \
      "-Dclassworlds.conf=${APP_HOME}/bin/app.conf" \
      "-Dapp.home=${APP_HOME}"  \
      org.codehaus.plexus.classworlds.launcher.Launcher start ${APP_CONFIG} "$@"
}

do_start() {
    check_app_pid
    mkdir -p "$(dirname "${CONSOLE_OUT}")" || exit 1
    touch "$CONSOLE_OUT" || exit 1
    eval "$JAVA_CMD" $JAVA_OPTS $JPDA_OPTS -classpath "${APP_HOME}"/boot/plexus-classworlds-*.jar \
      "-Dclassworlds.conf=${APP_HOME}/bin/app.conf" \
      "-Dapp.home=${APP_HOME}"  \
      org.codehaus.plexus.classworlds.launcher.Launcher start ${APP_CONFIG} "$@" \
      >> "${CONSOLE_OUT}" 2>&1 "&"

    if [ ! -z "$APP_PID" ]; then
      echo $! > "$APP_PID"
    fi
    echo "RsfCenter started."
}

#stop rsfCenter
do_stop() {
    ## check process
    if [ ! -z "$APP_PID" ]; then
      if [ -f "$APP_PID" ]; then
        if [ -s "$APP_PID" ]; then
          kill -0 `cat "$APP_PID"` >/dev/null 2>&1
          if [ $? -gt 0 ]; then
            echo "PID file found but no matching process was found. Stop aborted."
            exit 1
          fi
        else
          echo "PID file is empty and has been ignored."
        fi
      else
        echo "\$APP_PID was set but the specified file does not exist. Is RsfCenter running? Stop aborted."
        exit 1
      fi
    fi
    
    ##
    ## use manager stop it
    mkdir -p "$(dirname "${CONSOLE_OUT}")" || exit 1
    touch "$CONSOLE_OUT" || exit 1
    eval "$JAVA_CMD" $JAVA_OPTS -classpath "${APP_HOME}"/boot/plexus-classworlds-*.jar \
      "-Dclassworlds.conf=${APP_HOME}/bin/app.conf" \
      "-Dapp.home=${APP_HOME}"  \
      org.codehaus.plexus.classworlds.launcher.Launcher stop ${APP_CONFIG} "$@"

    ## stop failed. Shutdown port disabled? Try a normal kill.
    if [ $? != 0 ]; then
      if [ ! -z "$APP_PID" ]; then
        echo "The stop command failed. Attempting to signal the process to stop through OS signal."
        kill -15 `cat "$APP_PID"` >/dev/null 2>&1
      fi
    fi
    
    ##
    if [ "$1" = "-f" ]; then
      KILL_SLEEP_INTERVAL=5
      if [ -f "$APP_PID" ]; then
        PID=`cat "$APP_PID"`
        echo "Killing RsfCenter with the PID: $PID"
        kill -9 $PID
        while [ $KILL_SLEEP_INTERVAL -ge 0 ]; do
          kill -0 `cat "$APP_PID"` >/dev/null 2>&1
          if [ $? -gt 0 ]; then
            rm -f "$APP_PID" >/dev/null 2>&1
            if [ $? != 0 ]; then
              if [ -w "$APP_PID" ]; then
                cat /dev/null > "$APP_PID"
              else
                echo "The PID file could not be removed."
              fi
            fi
            echo "The RsfCenter process has been killed."
            break
          fi
          if [ $KILL_SLEEP_INTERVAL -gt 0 ]; then
              sleep 1
          fi
          KILL_SLEEP_INTERVAL=`expr $KILL_SLEEP_INTERVAL - 1 `
        done
        if [ $KILL_SLEEP_INTERVAL -lt 0 ]; then
            echo "RsfCenter has not been killed completely yet. The process might be waiting on some system call or might be UNINTERRUPTIBLE."
        fi
      fi
    fi
}

if [ "$1" = "start" ] ; then
  shift
  do_start $@

elif [ "$1" = "run" ] ; then
  shift
  do_exec $@

elif [ "$1" = "stop" ] ; then
  shift
  do_stop $@

elif [ "$1" = "version" ] ; then
  shift
  do_version $@

else
  echo "Usage: catalina.sh ( commands ... )"
  echo "commands:"
  echo "  start     Start Catalina in a separate window"
  echo "  stop      Stop Catalina, waiting up to 5 seconds for the process to end"
  echo "  stop -f   Stop Catalina, wait up to 5 seconds and then use kill -KILL if still running"
  echo "  version   What version of rsfCenter are you running?"
  exit 1
fi
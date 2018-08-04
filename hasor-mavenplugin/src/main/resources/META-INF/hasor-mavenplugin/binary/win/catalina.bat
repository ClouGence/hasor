@echo off
rem Licensed to the Apache Software Foundation (ASF) under one or more
rem contributor license agreements.  See the NOTICE file distributed with
rem this work for additional information regarding copyright ownership.
rem The ASF licenses this file to You under the Apache License, Version 2.0
rem (the "License"); you may not use this file except in compliance with
rem the License.  You may obtain a copy of the License at
rem
rem     http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.
rem -----------------------------------------------------------------------------
rem
rem Control Script for the RsfCenter Server
rem
rem Required ENV vars:
rem ------------------
rem   APP_HOME   - location of app's installed home dir
rem   JAVA_HOME - location of a JDK home dir
rem   JAVA_OPTS - parameters passed to the Java VM when running app
rem     e.g. to debug App itself, use
rem       set APP_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
rem -----------------------------------------------------------------------------
rem
rem

setlocal

rem Suppress Terminate batch job on CTRL+C
rem Guess APP_HOME if not defined
cd %~dp0
set "CURRENT_DIR=%cd%"

if not "%APP_HOME%" == "" goto gotHome
set "APP_HOME=%CURRENT_DIR%"
if exist "%APP_HOME%\bin\catalina.bat" goto okHome
cd ..
set "APP_HOME=%cd%"
cd "%CURRENT_DIR%"

:gotHome
if exist "%APP_HOME%\bin\catalina.bat" goto okHome
echo The APP_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end

:okHome
rem Get standard environment variables
if not exist "%APP_HOME%\bin\setenv.bat" goto setenvDone
call "%APP_HOME%\bin\setenv.bat"
if errorlevel 1 goto end

:setenvDone
if not "%JPDA_ENABLE%" == "jpda" goto noJpda
set JPDA=jpda
if not "%JPDA_TRANSPORT%" == "" goto gotJpdaTransport
set JPDA_TRANSPORT=dt_socket
:gotJpdaTransport
if not "%JPDA_ADDRESS%" == "" goto gotJpdaAddress
set JPDA_ADDRESS=8000
:gotJpdaAddress
if not "%JPDA_SUSPEND%" == "" goto gotJpdaSuspend
set JPDA_SUSPEND=n
:gotJpdaSuspend
if not "%JPDA_OPTS%" == "" goto gotJpdaOpts
set JPDA_OPTS=-agentlib:jdwp=transport=%JPDA_TRANSPORT%,address=%JPDA_ADDRESS%,server=y,suspend=%JPDA_SUSPEND%

:gotJpdaOpts
:noJpda

rem javaHome
rem In debug mode we need a real JDK (JAVA_HOME)
if ""%1"" == "debug" goto needJavaHome
rem Otherwise either JRE or JDK are fine
if not "%JRE_HOME%" == "" goto gotJreHome
if not "%JAVA_HOME%" == "" goto gotJavaHome
echo Neither the JAVA_HOME nor the JRE_HOME environment variable is defined
echo At least one of these environment variable is needed to run this program
goto exit

:needJavaHome
rem Check if we have a usable JDK
if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if not exist "%JAVA_HOME%\bin\javaw.exe" goto noJavaHome
if not exist "%JAVA_HOME%\bin\jdb.exe" goto noJavaHome
if not exist "%JAVA_HOME%\bin\javac.exe" goto noJavaHome
set "JRE_HOME=%JAVA_HOME%"
goto okJava

:noJavaHome
echo The JAVA_HOME environment variable is not defined correctly.
echo It is needed to run this program in debug mode.
echo NB: JAVA_HOME should point to a JDK not a JRE.
goto exit

:gotJavaHome
rem No JRE given, use JAVA_HOME as JRE_HOME
set "JRE_HOME=%JAVA_HOME%"

:gotJreHome
rem Check if we have a usable JRE
if not exist "%JRE_HOME%\bin\java.exe" goto noJreHome
if not exist "%JRE_HOME%\bin\javaw.exe" goto noJreHome
goto okJava

:noJreHome
rem Needed at least a JRE
echo The JRE_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto exit

:okJava
rem Don't override JAVA_CMD if the user has set it previously
if not "%JAVA_CMD%" == "" goto okjavaCMD
rem Set standard command for invoking Java.
rem Also note the quoting as JRE_HOME may contain spaces.
set JAVA_CMD="%JRE_HOME%\bin\java.exe"

:okjavaCMD
rem ----- Execute The Requested Command ---------------------------------------
set ACTION=%1
shift
echo Using   JAVA_HOME: "%JRE_HOME%"
echo Using    APP_HOME: "%APP_HOME%"
echo Using CONSOLE_OUT: "%CONSOLE_OUT%"
echo Using      Action: "%ACTION%"

@REM Decide how to startup depending on the version of windows
@REM -- Windows NT with Novell Login
if "%OS%"=="WINNT" goto WinNTNovell
@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg
:WinNTNovell
@REM -- 4NT shell
if "%@eval[2+2]" == "4" goto 4NTArgs
@REM -- Regular WinNT shell
set CMD_LINE_ARGS=%*
goto endInit
@REM The 4NT Shell from jp software
:4NTArgs
set CMD_LINE_ARGS=%$
goto endInit
:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of agruments (up to the command line limit, anyway).
set CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto endInit
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto Win9xApp
@REM Reaching here means variables are defined and arguments have been captured
:endInit

:classworlds
rem CLASSWORLDS_JAR
for %%i in ("%APP_HOME%"\boot\plexus-classworlds-*) do set CLASSWORLDS_JAR="%%i"
goto runapp

:runapp
if "%ACTION%" == "start" goto doStart
if "%ACTION%" == "stop" goto doStop
if "%ACTION%" == "version" goto doVersion

echo "Usage: catalina.sh ( commands ... )"
echo "commands:"
echo "  start     Start Catalina in a separate window"
echo "  stop      Stop Catalina, waiting up to 5 seconds for the process to end"
echo "  version   What version of rsfCenter are you running?"
goto end


:doStart
echo --doStart--
goto run

:doStop
echo --doStop--
goto run

:doVersion
echo --doVersion--
goto run

:run
set CLASSPATH=-classpath %CLASSWORLDS_JAR%
set CLASSWORLDS="-Dclassworlds.conf=%APP_HOME%\bin\app.conf" "-Dapp.home=%APP_HOME%"
call %JAVA_CMD% %JAVA_OPTS% %JPDA_OPTS% %CLASSPATH% %CLASSWORLDS% org.codehaus.plexus.classworlds.launcher.Launcher %ACTION% %APP_CONFIG% %CMD_LINE_ARGS%

:end
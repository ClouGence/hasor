@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM APP Start Up Batch script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM APP_HOME - location of app's installed home dir
@REM APP_OPTS - parameters passed to the Java VM when running App
@REM     e.g. to debug App itself, use
@REM set APP_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM ----------------------------------------------------------------------------

@echo off
@REM set %HOME% to equivalent of $HOME
if "%HOME%" == "" (set "HOME=%HOMEDRIVE%%HOMEPATH%")

set ERROR_CODE=0

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal
if "%OS%"=="WINNT" @setlocal

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%" == "" goto OkJHome

echo.
echo Error: JAVA_HOME not found in your environment. >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto chkHome

echo.
echo Error: JAVA_HOME is set to an invalid directory. >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:chkHome
if not "%APP_HOME%"=="" goto valHome

if "%OS%"=="Windows_NT" SET "APP_HOME=%~dp0.."
if "%OS%"=="WINNT" SET "APP_HOME=%~dp0.."
if not "%APP_HOME%"=="" goto valHome

echo.
echo Error: APP_HOME not found in your environment. >&2
echo Please set the APP_HOME variable in your environment to match the >&2
echo location of the App installation. >&2
echo.
goto error

:valHome

:stripHome
if not "_%APP_HOME:~-1%"=="_\" goto checkBat
set "APP_HOME=%APP_HOME:~0,-1%"
goto stripHome

:checkBat
if exist "%APP_HOME%\bin\app-start.bat" goto init

echo.
echo Error: APP_HOME is set to an invalid directory. >&2
echo APP_HOME = "%APP_HOME%" >&2
echo Please set the APP_HOME variable in your environment to match the >&2
echo location of the App installation >&2
echo.
goto error
@REM ==== END VALIDATION ====

:init
@REM Decide how to startup depending on the version of windows

@REM -- Windows NT with Novell Login
if "%OS%"=="WINNT" goto WinNTNovell

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

:WinNTNovell

@REM -- 4NT shell
if "%@eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set APP_CMD_LINE_ARGS=%*
goto endInit

@REM The 4NT Shell from jp software
:4NTArgs
set APP_CMD_LINE_ARGS=%$
goto endInit

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of agruments (up to the command line limit, anyway).
set APP_CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto endInit
set APP_CMD_LINE_ARGS=%APP_CMD_LINE_ARGS% %1
shift
goto Win9xApp

@REM Reaching here means variables are defined and arguments have been captured
:endInit
SET APP_JAVA_EXE="%JAVA_HOME%\bin\java.exe"

@REM -- 4NT shell
if "%@eval[2+2]" == "4" goto 4NTCWJars

@REM -- Regular WinNT shell
for %%i in ("%APP_HOME%"\boot\plexus-classworlds-*) do set CLASSWORLDS_JAR="%%i"
goto runapp

@REM The 4NT Shell from jp software
:4NTCWJars
for %%i in ("%APP_HOME%\boot\plexus-classworlds-*") do set CLASSWORLDS_JAR="%%i"
goto runapp

@REM Start App
:runapp
echo JVM is Run...
set CLASSWORLDS_LAUNCHER=org.codehaus.plexus.classworlds.launcher.Launcher
set APP_CONFIG_FILE="rsf-config.xml"

%APP_JAVA_EXE% %APP_OPTS% -classpath %CLASSWORLDS_JAR% "-Dclassworlds.conf=%APP_HOME%\bin\app.conf" "-Dapp.home=%APP_HOME%" %CLASSWORLDS_LAUNCHER% %APP_CONFIG_FILE% %APP_CMD_LINE_ARGS%

pause;

if ERRORLEVEL 1 goto error
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
if "%OS%"=="WINNT" @endlocal
set ERROR_CODE=1

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT
if "%OS%"=="WINNT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set APP_JAVA_EXE=
set APP_CMD_LINE_ARGS=

:endNT
@endlocal & set ERROR_CODE=%ERROR_CODE%

cmd /C exit /B %ERROR_CODE%


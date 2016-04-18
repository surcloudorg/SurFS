/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
@echo off
rem --------------------NAS-client----------------------

setlocal enabledelayedexpansion
for /f "tokens=1,2,3 delims==" %%i in (../surmount.conf) do (
 if "%%i"=="wrapper.java.command" set java_cmd=%%j
 if "%%i"=="wrapper.java.initmemory" set java_opts=-Xms%%jM
 if "%%i"=="wrapper.java.maxmemory" set java_opts=!java_opts! -Xmx%%jM
 if "%%i"=="wrapper.java.additional.1" set java_opts=!java_opts! %%j=%%k
 if "%%i"=="wrapper.java.additional.2" set java_opts=!java_opts! %%j=%%k
 if "%%i"=="wrapper.java.additional.3" set java_opts=!java_opts! %%j=%%k
 if "%%i"=="wrapper.java.additional.4" set java_opts=!java_opts! %%j=%%k
 if "%%i"=="wrapper.java.classpath.1" set classpath=%%j
 if "%%i"=="wrapper.app.parameter.1" set java_args=%%j
)

set mainclass=org.alfresco.jlan.app.JLANServer
set java_cmd=%java_cmd:/=\%
set cmd=%java_cmd% %java_opts% -classpath %classpath% %mainclass% %java_args%
echo cmd: %cmd%
echo enter 'x' to shutdown server, 'r' to restart server ...
%cmd%

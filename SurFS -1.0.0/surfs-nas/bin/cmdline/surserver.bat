@echo off
rem --------------------NAS-server----------------------

setlocal enabledelayedexpansion
for /f "tokens=1,2,3 delims==" %%i in (../surserver.conf) do (
 if "%%i"=="wrapper.java.command" set java_cmd=%%j
 if "%%i"=="wrapper.java.initmemory" set java_opts=-Xms%%jM
 if "%%i"=="wrapper.java.maxmemory" set java_opts=!java_opts! -Xmx%%jM
 if "%%i"=="wrapper.java.additional.1" set java_opts=!java_opts! %%j=%%k
 if "%%i"=="wrapper.java.additional.2" set java_opts=!java_opts! %%j=%%k
 if "%%i"=="wrapper.java.additional.3" set java_opts=!java_opts! %%j=%%k
 if "%%i"=="wrapper.java.additional.4" set java_opts=!java_opts! %%j=%%k
 if "%%i"=="wrapper.java.additional.5" set java_opts=!java_opts! %%j=%%k
 if "%%i"=="wrapper.java.classpath.1" set classpath=%%j
 if "%%i"=="wrapper.java.classpath.2" set classpath=!classpath!;%%j
 if "%%i"=="wrapper.java.classpath.3" set classpath=!classpath!;%%j
 if "%%i"=="wrapper.java.classpath.4" set classpath=!classpath!;%%j
 if "%%i"=="wrapper.app.parameter.1" set java_args=%%j=%%k
 if "%%i"=="wrapper.app.parameter.2" set java_args=!java_args! %%j=%%k
 if "%%i"=="wrapper.java.mainclass" set mainclass=%%j
)

set java_cmd=%java_cmd:/=\%
set cmd=%java_cmd% %java_opts% -classpath %classpath% %mainclass% %java_args%
echo cmd: %cmd%
%cmd%
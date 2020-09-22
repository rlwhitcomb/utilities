@echo off
setlocal
if exist DumpManifest*.class java DumpManifest %*&exit /b %errorlevel%

set UTIL_JAR=%~dp0utilities.jar
if not exist %UTIL_JAR% echo Unable to locate the required "utilities.jar" file in the "%~dp0" directory.&exit /b 1
java -cp %UTIL_JAR% DumpManifest %*
endlocal


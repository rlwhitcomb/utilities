@echo off
if "%JAR_FILE%" == "" set JAR_FILE=utilities.jar
set JAR_PATH=%~dp0%JAR_FILE%
if not exist %JAR_PATH% echo Unable to locate the required "%JAR_FILE%" file in the "%~dp0" directory.&exit /b 1
if "%CLASSPATH%" == "" (
   set FULL_CLASSPATH=%JAR_PATH%
) else (
   set FULL_CLASSPATH=%JAR_PATH%;%CLASSPATH%
)
java -cp %FULL_CLASSPATH% %JVM_ARGS% %CLASS_NAME% %CMD_ARGS% %*

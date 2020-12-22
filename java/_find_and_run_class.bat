@echo off
if "%JAR_FILE%" == "" set JAR_FILE=utilities.jar
set JAR_PATH=%~dp0%JAR_FILE%
if not exist %JAR_PATH% echo Unable to locate the required "%JAR_FILE%" file in the "%~dp0" directory.&exit /b 1
java -cp %JAR_PATH%;%CLASSPATH% %CLASS_NAME% %*

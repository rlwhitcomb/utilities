@echo off
if "%JAR_FILE%" == "" set JAR_FILE=utilities.jar
set JAR_PATH=%~dp0%JAR_FILE%
if not exist %JAR_PATH% echo Unable to locate the required "%JAR_FILE%" file in the "%~dp0" directory.&exit /b 1
if "%CLASSPATH%" == "" (
   set FULL_CLASSPATH=%JAR_PATH%
) else (
   set FULL_CLASSPATH=%JAR_PATH%;%CLASSPATH%
)
for %%F in (%~dp0*.jar) do call :addclass %%F
if "%CLASS_NAME%" == "" (
   start javaw -jar %JAR_PATH% %JVM_ARGS% %CMD_ARGS% %*
) else (
   start javaw -cp %FULL_CLASSPATH% %JVM_ARGS% %CLASS_NAME% %CMD_ARGS% %*
)
exit /b %errorlevel%

:addclass
if not "%1" == "%JAR_PATH%" set FULL_CLASSPATH=%FULL_CLASSPATH%;%1
exit /b


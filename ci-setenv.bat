@echo off
set PROJECT_HOME=%CD%
set UTILITIES_HOME=%PROJECT_HOME%\dist
if not exist %UTILITIES_HOME% mkdir %UTILITIES_HOME%
set PATH=%UTILITIES_HOME%;%PATH%

:: Setup the CLASSPATH with everything we will need
set CLASSPATH=.;%PROJECT_HOME%\java
for %%J in (%PROJECT_HOME%\java\external-files\*.jar) do call :addclass %%J
exit /b 0

:addclass
set CLASSPATH=%CLASSPATH%;%1
exit /b

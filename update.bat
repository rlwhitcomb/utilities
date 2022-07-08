@echo off
if "%1" == "-?" goto :help
if "%1" == "-help" goto :help
if "%1" == "?" goto :help

call ant -f update.xml %*
if errorlevel 1 exit /b %errorlevel%
cd utilities-master\java
echo Doing full build with latest code...
call ant all-install
if errorlevel 1 exit /b %errorlevel%
echo Done.
exit /b 0

:help
echo.
echo Usage: %~n0 ( -Dcommit=xxxxxxx )
echo.
echo ^ ^ Downloads the latest source .zip from github, unzip, and do the complete build.
echo.

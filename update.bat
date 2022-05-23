@echo off
call ant -f update.xml
if errorlevel 1 exit /b %errorlevel%
cd utilities-master\java
echo Doing full build with latest code...
call ant all-install
if errorlevel 1 exit /b %errorlevel%
echo Done.
exit /b 0

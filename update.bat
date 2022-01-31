@echo off
setlocal
::
:: Parameter is the new revision tag to use
::
if "%1" == "" goto usage
set COMMIT=%1
call ant unzip
if errorlevel 1 exit /b %errorlevel%
cd utilities-master\java
echo Fixing up the latest revision tag...
call ant fixup -Dcommit=%COMMIT%
echo Doing full build with latest code...
call ant all-install
echo Done.
exit /b 0

:usage
echo Usage: %0 _latest_commit_tag_

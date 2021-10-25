@echo off
call ant unzip
if errorlevel 1 exit /b %errorlevel%
cd utilities-master\java
echo Run "ant fixup -Dcommit=xxxxxxx" where "xxxxxxx" is the latest git revision,
echo then run "ant all-install" to finish the installation.
echo.


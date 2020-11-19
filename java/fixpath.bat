@echo off
for /F "tokens=*" %%P in ('java -cp %~dp0utilities.jar FixPath') do set PATH=%%P
echo PATH=%PATH%

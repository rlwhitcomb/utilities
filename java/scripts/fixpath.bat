@echo off
for /F "tokens=*" %%P in ('java -cp %~dp0utilities.jar info.rlwhitcomb.tools.FixPath') do set PATH=%%P

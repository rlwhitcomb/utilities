@echo off
setlocal
set CLASS_NAME=info.rlwhitcomb.tools.Hash
set CMD_ARGS=--algorithm=MD5
call %~dp0_find_and_run_class %*
endlocal
exit /b %errorlevel%

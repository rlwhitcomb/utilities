@echo off
setlocal
set CLASS_NAME=MD5
set CMD_ARGS=--algorithm=SHA-256
call %~dp0_find_and_run_class %*
endlocal

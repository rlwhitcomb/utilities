@echo off
setlocal
set CLASS_NAME=net.iharder.b64.Base64
set CMD_ARGS=-e
call %~dp0_find_and_run_class %*
endlocal

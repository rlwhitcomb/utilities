@echo off
setlocal
set CLASS_NAME=Head
call %~dp0_find_and_run_class %*
endlocal

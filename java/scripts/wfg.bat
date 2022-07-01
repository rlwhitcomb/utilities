@echo off
setlocal
set CLASS_NAME=info.rlwhitcomb.wordfind.WordFind
set CMD_ARGS=-gui
call %~dp0_find_and_run_class %*
endlocal
exit /b %errorlevel%

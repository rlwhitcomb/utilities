@echo off
setlocal
set CLASS_NAME=info.rlwhitcomb.util.Environment
call %~dp0_find_and_run_class %*
endlocal
exit /b %errorlevel%

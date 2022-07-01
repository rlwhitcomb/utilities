@echo off
setlocal
call %~dp0_find_and_run_class %*
endlocal
exit /b %errorlevel%

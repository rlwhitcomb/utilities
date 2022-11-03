@echo off
setlocal
set CLASS_NAME=info.rlwhitcomb.calc.Calc
set CMD_ARGS=-nolibraries
call %~dp0_find_and_run_class %*
endlocal
exit /b %errorlevel%

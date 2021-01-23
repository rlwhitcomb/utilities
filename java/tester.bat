@echo off
setlocal
set CLASS_NAME=info.rlwhitcomb.tester.Tester
call %~dp0_find_and_run_class %*
endlocal

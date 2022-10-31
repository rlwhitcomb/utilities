@echo off
setlocal
set CLASS_NAME=info.rlwhitcomb.tester.Tester
set JVM_ARGS=@@@JVM17+@@@
call %~dp0_find_and_run_class %*
endlocal
exit /b %errorlevel%

@echo off
setlocal
set CLASS_NAME=info.rlwhitcomb.tree.Tree
call %~dp0_find_and_run_class %*
endlocal
exit /b %errorlevel%

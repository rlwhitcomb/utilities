@echo off
call b64 -e images\backspace_32.png -o back.b64
call b64 -d back.b64 -o back.png
call cmp back.png images\backspace_32.png
if errorlevel 1 exit /b %errorlevel%
del back.b64 back.png
exit /b 0


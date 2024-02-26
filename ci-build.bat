@echo off
call ci-setenv
echo Start of "ci-build.bat", current directory: %CD%
cd java
call ant -DCI_BUILD=true update doc test
if errorlevel 1 (type ..\dist\test*.log) && (exit /b 1)

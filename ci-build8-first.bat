@echo off
call ci-setenv
echo Start of "ci-build8-first.bat", current directory: %CD%
cd java
call ant -DCI_BUILD=true process-grammars

@echo off
for /R info\rlwhitcomb %%F in (*.txt) do call :check_file %%F && if errorlevel 1 exit /b %errorlevel%
exit /b 0

:check_file
findstr /V "^#.*" %1 >words.txt
findstr /V "^$" words.txt >words.txt
sort <words.txt >sort.txt
fc words.txt sort.txt >nul
if errorlevel 1 (
   echo The "%1" word list is NOT sorted correctly!
   exit /b 1
) else (
   for /F %%C in ('call wc %1 /w') do echo The "%1" word list is correctly sorted ^( %%C words ^).
   del words.txt sort.txt
   exit /b 0
)


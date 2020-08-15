@echo off
:: A small "navigation" utility to help you move around
:: in the directory structure without so much typing,
:: by knowing about a (small) set of frequently visited
:: locations.
:: The list of locations, and their descriptions is in the
:: "destinations" file in the same directory as this file.
:: Feel free to add to that list as you see fit.

if "%1"=="" goto display_help
if /I "%1" EQU "/help" goto display_help
if /I "%1" EQU "help" goto display_help
if /I "%1" EQU "/h" goto display_help
if /I "%1" EQU "/?" goto display_help
if /I "%1" EQU "?" goto display_help

if /I "%1" EQU "home" goto check_going_home_time

:check_destination
set COMMAND=
for /F "delims=, tokens=1,2,3,*" %%I in (%~dp0destinations) do (
   for /f "tokens=*" %%M in  ('echo %%K') do (
      if /I "%1" EQU "%%J" set COMMAND=%%I %%M& goto execute_command
   )
)
:execute_command
if "%COMMAND%" EQU "" goto display_help
%COMMAND%
cd
exit /b 0

:check_going_home_time
for /F %%I in ('date /t') do set DOW=%%I
for /F "delims=:" %%I in ("%TIME%") do set HOD=%%I
if "%DOW%" EQU "Fri" goto check_friday
if "%HOD%" LSS "17" (echo Keep working!) && goto check_destination
goto it_is_time_to_go_home
:check_friday
if "%HOD%" LSS "12" (echo Keep working!) && goto check_destination
:it_is_time_to_go_home
echo Yes! It is time to go home!
exit /b 0

:display_help
setlocal enabledelayedexpansion
set "SPACES=             "
echo Usage: %~n0 LOCATION
echo.
echo Pushes your current directory and goes to the specified
echo LOCATION, where LOCATION is one of the following:
for /F "delims=, tokens=1,2,3,*" %%I in (%~dp0destinations) do (
   if "%%L" EQU "" (
      for /f "tokens=*" %%M in  ('echo %%K') do (
         set "key=%%J%SPACES%"
         set line=  !key:~0,9! = %%M
         echo !line!
      )
   ) else (
      for /f "tokens=*" %%M in  ('echo %%L') do (
         set "key=%%J%SPACES%"
         set line=  !key:~0,9! = %%M
         echo !line!
      )
   )
)
endlocal

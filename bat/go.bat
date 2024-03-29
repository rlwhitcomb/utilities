@echo off
::
:: The MIT License (MIT)
::
:: Copyright (c) 2020,2022 Roger L. Whitcomb.
::
:: Permission is hereby granted, free of charge, to any person obtaining a copy
:: of this software and associated documentation files (the "Software"), to deal
:: in the Software without restriction, including without limitation the rights
:: to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
:: copies of the Software, and to permit persons to whom the Software is
:: furnished to do so, subject to the following conditions:
::
:: The above copyright notice and this permission notice shall be included in all
:: copies or substantial portions of the Software.
::
:: THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
:: IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
:: FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
:: AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
:: LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
:: OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
:: SOFTWARE.
::
:: A small "navigation" utility to help you move around
:: in the directory structure without so much typing,
:: by knowing about a (small) set of frequently visited
:: locations.
:: The list of locations, and their descriptions is in the
:: "destinations-USERNAME" file in the same directory as this file.
:: Feel free to add to that list as you see fit.

set DESTINATIONS=%~dp0destinations-%USERNAME%
if not exist "%DESTINATIONS%" goto setup

if "%1"=="" goto display_help
if /I "%1" EQU "/help" goto display_help
if /I "%1" EQU "help" goto display_help
if /I "%1" EQU "/h" goto display_help
if /I "%1" EQU "/?" goto display_help
if /I "%1" EQU "?" goto display_help

if /I "%1" EQU "home" goto check_going_home_time

:check_destination
set COMMAND=
for /F "delims=, tokens=1,2,3,*" %%I in (%DESTINATIONS%) do (
   if "%%K" EQU " " (
      call :parse_aliases %1 "%%J" %%I
      if errorlevel 1 set DEST=&goto execute_command
   ) else (
      for /f "tokens=*" %%M in ('echo %%K') do (
         call :parse_aliases %1 "%%J" %%I
         if errorlevel 1 set DEST=%%M&goto execute_command
      )
   )
)
:execute_command
if "%COMMAND%" EQU "" goto display_help
if /I "%COMMAND%" EQU "popd" goto doit
:: Also, "pushd" with no destination can also be used to list the saved locations
if /I "%COMMAND%" EQU "pushd" (
   if "%DEST%" EQU "" goto doit
)
:: Also, "cd" with no destination can also be used to print the current directory
if /I "%COMMAND%" EQU "cd" (
   if "%DEST%" EQU "" goto doit
)
:: For "cd" or "pushd" commands, we can add any number of subdirectories
:: below the specified one (for convenience).
:add_subdir
shift
if "%1" EQU "" goto check_dir
if "%1" EQU "-" (
   set DEST=%DEST%\..
) else (
   if "%1" EQU "--" (
      set DEST=%DEST%\..\..
   ) else (
      if "%1" EQU "---" (
         set DEST=%DEST%\..\..\..
      ) else (
         set DEST=%DEST%\%1
      )
   )
)
goto add_subdir
:check_dir
if exist "%DEST%" goto doit
echo Location "%DEST%" does not exist!
exit /b 1
:doit
%COMMAND% "%DEST%"
exit /b 0

:parse_aliases
set LIST=%2
set LIST=%LIST:"=%
:parse2
FOR /F "delims=; tokens=1*" %%P IN ("%LIST%") DO (
   if not "%%P" == "" (
      if /I "%1" EQU "%%P" set COMMAND=%3&exit /b 1
   )
   if not "%%Q" == "" set LIST=%%Q&goto parse2
)
exit /b

:setup
echo The %~n0 command relies on a per-user configuration file named
echo ^ ^ ^ %DESTINATIONS%
echo which does not exist.
echo Please create this file and try the command again.
echo.
echo The format of this file is as follows (4 fields per line):
echo ^ ^ ^ cmd,alias,directory,description
echo.
echo where "cmd" is one of "cd", "cd /d", pushd", or "popd"
echo ^ ^ and "alias" is the shortcut name you want to use for this location
echo.
echo and where "directory" should be a single blank for "popd" (or "pushd" when
echo it is used to list the saved destinations)
echo.
echo Note: if "description" is omitted then the directory name itself will
echo ^ ^ ^ ^ ^ ^ be used as the description (but the comma must still be there).
echo.
echo Environment variables can be specified and will be substituted
echo as needed.
echo.
echo For example:
echo cd /d,home,%%USERPROFILE%%,Your %%USERPROFILE%% directory.
echo.
exit /b 1

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
set "SPACES=                    "
echo Usage: %~n0 LOCATION [subdirectory [subsubdir] ...] ^| help ^| ?
echo.
echo Pushes your current directory and goes to the specified
echo LOCATION, and any given subdirectory(ies) beneath it,
echo where LOCATION is one of the following:
for /F "delims=, tokens=1,2,3,*" %%I in (%DESTINATIONS%) do (
   if "%%L" EQU "" (
      for /f "tokens=*" %%M in ('echo %%K') do (
         set "key=%%J%SPACES%"
         set line=  !key:~0,15! = %%M
         echo !line!
      )
   ) else (
      for /f "tokens=*" %%M in ('echo %%L') do (
         set "key=%%J%SPACES%"
         set line=  !key:~0,15! = %%M
         echo !line!
      )
   )
)
echo.
endlocal


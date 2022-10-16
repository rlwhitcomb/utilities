@echo off
setlocal

set CANON=
set ACTUAL=

if "%1" equ "" goto :usage
if not exist "%1" (echo [1;91mInput file "%1" not found![0m) & (echo.) & (goto :usage)
set HASH=sha512
if "%2" equ "" goto :check_hash_exists
if /i "%2" equ "-sha512" goto :check_hash_exists
if /i "%2" equ "-sha256" (set HASH=sha256) & (goto :check_hash_exists)
if /i "%2" equ "-sha1" (set HASH=sha1) & (goto :check_hash_exists)
if /i "%2" equ "-md5" (set HASH=md5) & (goto :check_hash_exists)
echo [1;91mUnknown hash algorithm "%2"![0m
echo.
goto usage

:check_hash_exists
set INPUT=%1.%HASH%
if not exist %INPUT% goto :hash_not_found

set CANON=%TEMP%\%INPUT%
set ACTUAL=%TEMP%\%1.hash

:: make sure the canon file is in the right format for comparison
call lists --single --lower %INPUT% > %CANON%
call %HASH% --lower %1 > %ACTUAL%
call cmp /silent %CANON% %ACTUAL%
if errorlevel 1 goto :error

echo [1;92m%HASH% checksum of %1 verified correct.[0m
goto :leave

:error
echo [1;91mExpected hash value:[0m
type %CANON%
echo [1;91mActual hash value:[0m
type %ACTUAL%
goto :leave

:hash_not_found
echo [1;91mExpected hash file "%INPUT%" not found![0m
echo.

:usage
echo Usage: %~n0 _file_name_to_check_ [-sha512^|-sha256^|-sha1^|-md5]
echo.
echo ^ ^ ^ where "-sha512" is the default hash algorithm to use
echo ^ ^ ^ and there should be a "*.sha512" file with the expected hash value
echo ^ ^ ^ (similarly a *.sha256, *.sha1, or *.md5 file for a different hash).

:leave
if "%CANON%" equ "" goto :end
if exist %CANON% del %CANON%
if exist %ACTUAL% del %ACTUAL%
:end
endlocal


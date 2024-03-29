@echo off
:: Verify the calculation of Mersenne prime M43112609
set NUMBER=%1
set DIGITS=%2
if exist test\data\M%NUMBER%.txt.gz call gunz -keep -out M%NUMBER%.txt test\data\M%NUMBER%.txt.gz
call lists -single M%NUMBER%.txt >ref.txt
call c -nocolor -noseps -sense -r "$unl;(2**%NUMBER%)-1" >calc.txt
for /f %%f in ('call c -nocolor verify') do set count=%%f
if /I %count% neq %DIGITS% (
   echo Digit count is different!
   echo Reference number of digits is %DIGITS%, calculated number is %count%.
)
call cmp -c -b -q ref.txt calc.txt
if errorlevel 1 (
   echo Differences found!
   echo Reference value in "ref.txt", calculated value in "calc.txt"
   echo Original data in "M%NUMBER%.txt"
   exit /b 1
)
echo No differences for M%NUMBER% ^(%count% digits^)! Calculation is correct.
del M%NUMBER%.txt ref.txt calc.txt
exit /b 0


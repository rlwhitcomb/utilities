@echo off
:: Verify the calculation of Mersenne number M13699727 divisible by 3067752467657
set NUMBER=13699727
if exist test\data\M%NUMBER%.txt.gz call gunz -keep -out m.txt test\data\M%NUMBER%.txt.gz
call c test\files\m.calc -outputcharset UTF-8 -output m2.txt
call cmp -c -b -q --ignore-line-endings m.txt m2.txt
if errorlevel 1 (
   echo Differences found!
   echo Reference value in "m.txt", calculated value in "m2.txt"
   exit /b 1
)
echo No differences for M%NUMBER% / 3067752467657! Calculation is correct.
del m.txt m2.txt
exit /b 0


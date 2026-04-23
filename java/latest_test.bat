@echo off
for /f "usebackq" %%F in (`call c -noc -nol -clr -res -8 -- test*.log -- "max($*) @q"`) do notepad %%F

@echo off
for /f "usebackq" %%F in (`call c -noc -nol -clr -res -- test*.log -- "max($*) @q"`) do notepad %%F

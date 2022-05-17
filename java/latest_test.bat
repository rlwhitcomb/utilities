@echo off
for /f "usebackq" %%F in (`call c -noc -clr -res -- test*.log -- "max($*) @q"`) do notepad %%F

@echo off
for /f "usebackq" %%F in (`call c -noc -res -- test*.log -- "max(sort($*)) @q"`) do notepad %%F

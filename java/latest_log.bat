@echo off
for /f "usebackq" %%F in (`call c -nocol -- test*.log -- ":results on;max(sort($*)) @q"`) do notepad %%F

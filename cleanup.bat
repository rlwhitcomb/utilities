@echo off
for /D %%F in (rlwhitcomb-utilities-*) do (
  echo Cleaning up %%F directory ...
  cd %%F
  del .gitattributes .gitignore
  cd ..
  rmdir %%F
)

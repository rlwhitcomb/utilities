#!/bin/bash
. ci-setenv
cd /home/runner/work/utilities/utilities/java
ls
ant -DCI_BUILD=true clean update doc test
if [[ $? -ne 0 ]]
then
   cat dist/test*.log
   exit 1
fi
echo Distribution Files
echo ------------------
ls -al dist

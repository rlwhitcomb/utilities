#!/bin/bash
. ci-setenv
cd /home/runner/work/utilities/utilities/java
echo Current directory listing
echo -------------------------
ls -al
echo Current environment
echo -------------------
env | grep -i HOME
echo
ant -DCI_BUILD=true clean update doc test
if [[ $? -ne 0 ]]
then
   cat dist/test*.log
   exit 1
fi
echo Distribution Files
echo ------------------
ls -al dist

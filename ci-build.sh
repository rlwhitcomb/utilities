#!/bin/bash
. ci-setenv
echo Start of "ci-build.sh", current directory: `pwd`
cd /home/runner/work/utilities/utilities/java
ant -DCI_BUILD=true clean update doc test
if [[ $? -ne 0 ]]
then
   cat ../dist/test*.log
   exit 1
fi

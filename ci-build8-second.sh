#!/bin/bash
. ci-setenv
echo Start of "ci-build8-second.sh", current directory: `pwd`
cd java
ant -DCI_BUILD=true update doc test
if [[ $? -ne 0 ]]
then
   cat ../dist/test*.log
   exit 1
fi

#!/bin/bash -e
. ci-setenv
cd /home/runner/work/utilities/utilities/java
ls
ant -DCI_BUILD=true clean update doc test


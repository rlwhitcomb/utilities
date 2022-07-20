#!/bin/bash
. ci-setenv
echo Start of "ci-build8-first.sh", current directory: `pwd`
cd /home/runner/work/utilities/utilities/java
ant -DCI_BUILD=true clean process-grammars

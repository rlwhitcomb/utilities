#!/bin/bash -e
. ci-setenv
cd /home/runner/work/utilities/utilities/java
ls
ant clean update doc test


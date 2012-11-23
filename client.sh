#!/bin/bash
source ./compile.sh
java -classpath "$path" ChatClient $1

#!/bin/sh
if [ "$1" = "client" ]
then
    d='ChatClient'
else
    d='ChatServer'
fi
javac $d.java && java $d $2

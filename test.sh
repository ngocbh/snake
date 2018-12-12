#!/bin/bash

./build.sh;

if [ $? -ne 0 ]
then
    echo "Error???"
    exit
fi

java -classpath bin com.test.Test src/test/in.txt
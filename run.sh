#!/bin/bash
./build.sh;
if [ $? -ne 0 ]
then
    echo "Error???"
    exit
fi
java -classpath bin com.gui.Main -width 15 -height 15 -speed 5 -algorithm 2;

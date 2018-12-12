#!/bin/bash
./build.sh;
if [ $? -ne 0 ]
then
    echo "Error???"
    exit
fi
java -classpath bin com.gui.Main -width 8 -height 8 -speed 50 -algorithm 4;

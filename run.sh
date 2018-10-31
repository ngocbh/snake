#!/bin/bash

./build.sh;
java -classpath bin com.gui.Main -width 10 -height 10 -speed 50 -algorithm 4;

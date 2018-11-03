#!/bin/bash

./build.sh;
java -classpath bin com.gui.Main -width 30 -height 30 -speed 50 -algorithm 4;

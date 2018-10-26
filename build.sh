#!/bin/bash

# javac -d bin src/ai/*.java;
# javac -d bin src/gui/*.java;

javac -classpath bin -d bin src/gui/Tuple.java;
javac -classpath bin -d bin src/gui/SubSquarePanel.java;
javac -classpath bin -d bin src/ai/*.java;
javac -classpath bin -d bin src/gui/*.java;
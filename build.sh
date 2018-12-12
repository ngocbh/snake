#!/bin/bash

# javac -d bin src/ai/*.java;
# javac -d bin src/gui/*.java;

#javac -classpath bin -d bin src/gui/Tuple.java;
#javac -classpath bin -d bin src/gui/SubSquarePanel.java;
#javac -classpath bin -d bin src/gui/Window.java;
#javac -classpath bin -d bin src/ai/Utils.java;
#javac -classpath bin -d bin src/gui/Snake.java;
#javac -classpath bin -d bin src/gui/DataOfSquare.java;
#javac -classpath bin -d bin src/gui/KeyboardListener.java;
#javac -classpath bin -d bin src/gui/ThreadsController.java src/ai/AutoPlay.java;
javac -classpath bin -d bin src/test/*.java;
javac -classpath bin -d bin src/ai/*.java src/gui/*.java;
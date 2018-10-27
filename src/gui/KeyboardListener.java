package com.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

 public class KeyboardListener extends KeyAdapter{
 	
 	public ThreadsController thread;

 	public KeyboardListener(ThreadsController thread) {
 		this.thread = thread;
 	}

	public void keyPressed(KeyEvent e){
	    switch(e.getKeyCode()){
    	case 39:	// -> Right 
    				//if it's not the opposite direction
    				if(thread.snake.directionSnake!=2) 
    					thread.snake.directionSnake=1;
    			  	break;
    	case 38:	// -> Top
					if(thread.snake.directionSnake!=4) 
						thread.snake.directionSnake=3;
    				break;
    				
    	case 37: 	// -> Left 
					if(thread.snake.directionSnake!=1)
						thread.snake.directionSnake=2;
    				break;
    				
    	case 40:	// -> Bottom
					if(thread.snake.directionSnake!=3)
						thread.snake.directionSnake=4;
    				break;
    	
    	default: 	break;
	    }
	}
 	
 }

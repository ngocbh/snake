package com.gui;

import java.awt.GridLayout;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JFrame;

import com.gui.*;

class GameFrame extends JFrame {
	private static final long serialVersionUID = -2542001418764869760L;
        Window window;
        int width, height, algorithm;
        long speed;
	public GameFrame(int width,int height,long speed,int algorithm) {
		//Creating the window with all its awesome snaky features
		this.width = width;
                this.height = height;
                this.speed = speed;
                this.algorithm = algorithm;
		window = new Window(width, height,speed, algorithm, this);
		
		getContentPane().setLayout(new GridLayout(1,1,0,0));
		getContentPane().setBackground(Color.white);
		getContentPane().add(window);
                
                
		//--------------------------------------------------------------
		// Add Player 1
		//--------------------------------------------------------------
		// initial position of the snake
//		Tuple position1 = new Tuple(10,10);
//		// passing this value to the controller
//		ThreadsController threadControllerPlayer1 = new ThreadsController(true,speed,position1);
//		threadControllerPlayer1.algorithm = algorithm;
//		//Let's start the game now..
//		threadControllerPlayer1.start();
                
		// Links the window to the keyboardlistenner.
		// f1.addKeyListener((KeyListener) new KeyboardListener(threadControllerPlayer1));


		// //--------------------------------------------------------------
		// // Add Player 2
		// //--------------------------------------------------------------
		// // initial position of the snake
		// Tuple position2 = new Tuple((int)(width/2),(int)(height/2));
		// // passing this value to the controller
		// ThreadsController threadControllerPlayer2 = new ThreadsController(false,200,position2);
		// //Let's start the game now..
		// threadControllerPlayer2.start();
		// // Links the window to the keyboardlistenner.
		// f1.addKeyListener((KeyListener) new KeyboardListener(threadControllerPlayer2));
		     
	}
        
        public void restart(Window win) {
            win.invalidate();
            win.setVisible(false);
            win.removeAll();
            getContentPane().remove(win);
            win = null;
            getContentPane().add(new Window(width,height,speed,algorithm,this));
            invalidate();
            validate();
        }
}
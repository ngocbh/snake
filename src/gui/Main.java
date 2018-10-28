package com.gui;

import javax.swing.JFrame;
import com.gui.Window;
import java.awt.event.KeyListener;

public class Main {
	static int width = 20;
	static int height = 20;
	static long speed = 20;

	public static void main(String[] args) {

		//Creating the window with all its awesome snaky features
		
		Window f1= new Window(width,height,speed);
		

		//--------------------------------------------------------------
		// Add Player 1
		//--------------------------------------------------------------
		// initial position of the snake
		Tuple position1 = new Tuple(2,2);
		// passing this value to the controller
		ThreadsController threadControllerPlayer1 = new ThreadsController(true,speed,position1);
		//Let's start the game now..
		threadControllerPlayer1.start();
		// Links the window to the keyboardlistenner.
		// this.addKeyListener((KeyListener) new KeyboardListener(c));


		// //--------------------------------------------------------------
		// // Add Player 2
		// //--------------------------------------------------------------
		// // initial position of the snake
		// Tuple position2 = new Tuple((int)(width/2),(int)(height/2));
		// // passing this value to the controller
		// ThreadsController threadControllerPlayer2 = new ThreadsController(true,speed,position2);
		// //Let's start the game now..
		// threadControllerPlayer2.start();
		// // Links the window to the keyboardlistenner.
		// // f1.addKeyListener((KeyListener) new KeyboardListener(threadControllerPlayer2));



		//Setting up the window settings
		f1.setTitle("Snake");
		f1.setSize(600,630);
		f1.setVisible(true);
		f1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);             

	}
}

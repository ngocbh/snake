package com.gui;

import javax.swing.JFrame;
import com.gui.Window;
import java.awt.event.KeyListener;

public class Main {
	static int width = 20;
	static int height = 20;
	static long speed = 20;

	//1 : BFS Proposal | 2 : Greedy base on BFS  | 3 : A star Proposal | 4 : Greedy base on A star
	public static int algorithm = 4;

	public static void main(String[] args) {
		System.out.println("[Usage] : -width [width] -height [height] -speed [speed] -algorithm [algorithm(1-4)]");

		for (int i = 0; i < args.length; i++) 
			if ( args[i].equals("-width") ) {
				width = Integer.parseInt(args[i+1]);
			} 
		for (int i = 0; i < args.length; i++) 
			if ( args[i].equals("-height") ) {
				height = Integer.parseInt(args[i+1]);
			} 
		for (int i = 0; i < args.length; i++) 
			if ( args[i].equals("-speed") ) {
				speed = Integer.parseInt(args[i+1]);
			} 
		for (int i = 0; i < args.length; i++) 
			if ( args[i].equals("-algorithm") ) {
				algorithm = Integer.parseInt(args[i+1]);
			} 

		GameFrame gameFrame = new GameFrame(width,height,speed,algorithm);
		//Setting up the window settings
		gameFrame.setTitle("Snake");
		gameFrame.setSize(30*height,30*(width+1));
		gameFrame.setVisible(true);
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

package com.gui;

import javax.swing.JFrame;
import com.gui.Window;

public class Main {

	public static void main(String[] args) {

		//Creating the window with all its awesome snaky features
		Window f1= new Window(20,20);
		
		//Setting up the window settings
		f1.setTitle("Snake");
		f1.setSize(600,630);
		f1.setVisible(true);
		f1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);             

	}
}

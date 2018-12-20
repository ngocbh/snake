package com.gui;

import java.awt.GridLayout;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JFrame;

import com.gui.*;
import com.ai.Utils;

public class Window extends JPanel {
	private static final long serialVersionUID = -2542001418764869760L;
	public static ArrayList<ArrayList<DataOfSquare>> Grid;
	public int width;
	public int height;
        public long speed;
        public int algorithm;
        GameFrame gameFrame;
        public int numOR;
	public Window(int width,int height,long spd, int alg, GameFrame gameFr, int num){
		numOR = num;
		this.width = width;
		this.height = height;
                speed = spd;
                algorithm = alg;
                gameFrame = gameFr;
		// Creates the arraylist that'll contain the threads
		Grid = new ArrayList<ArrayList<DataOfSquare>>();
		ArrayList<DataOfSquare> data;
		
		// Creates Threads and its data and adds it to the arrayList
		for(int i=0;i<width;i++){
			data= new ArrayList<DataOfSquare>();
			for(int j=0;j<height;j++){
				int color = (j==0||j==height-1||i==0||i==width-1)?3:0;
				DataOfSquare c = new DataOfSquare(color);
				data.add(c);
			}
			Grid.add(data);
		}
		
		// Setting up the layout of the panel
		this.setLayout(new GridLayout(this.width,this.height,0,0));
		setBackground(Color.white);
		// Start & pauses all threads, then adds every square of each thread to the panel
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				this.add(Grid.get(i).get(j));
			}
		}
                
                
		
	}

	public static int[][] convertSimpleGrid(ArrayList<ArrayList<DataOfSquare>> squares) {
		int h = squares.size();
		int w = squares.get(0).size();

		int[][] res = new int[h][w];
		for (int i = 0; i < h; i++) 
			for (int j = 0; j < w; j++)
				res[i][j] = squares.get(i).get(j).obj;
		return res;
	}
        
        public void startThread(int hx,int hy,long speed, boolean isAuto,int algorithm) {
            Tuple position1 = new Tuple(hx,hy);
            // passing this value to the controller
            ThreadsController threadControllerPlayer1 = new ThreadsController(isAuto,speed,position1,gameFrame,this,numOR,algorithm);
            //Let's start the game now..
            threadControllerPlayer1.start();
        }
}

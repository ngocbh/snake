package com.gui;

import java.util.ArrayList;

import com.ai.AutoPlay;
import com.ai.Utils;
import com.gui.*;

public class Snake {
	public Tuple headSnakePos;
	public Tuple tailSnakePos;
	public int sizeSnake=1;
	public int directionSnake;
	public ArrayList<Tuple> positions = new ArrayList<Tuple>();
	public int[][] state;

	public Snake() {
		sizeSnake = 1;
		positions = new ArrayList<Tuple>();
		state = Window.convertSimpleGrid(Window.Grid);
	}


	//Moves the head of the snake and refreshes the positions in the arraylist
	//1:right 2:left 3:top 4:bottom 0:nothing
	public void moveInterne(int dir){
		headSnakePos = headSnakePos.next(dir);
		positions.add(new Tuple(headSnakePos.x,headSnakePos.y));
	}

	//Refresh the squares that needs to be 
	public void moveExterne(){
		// System.out.println("========positions========");
		for(int i=0; i < positions.size(); i++) {
		 	Tuple t = positions.get(i);
		 	// t.print();
		 	state[t.x][t.y] = 2;
		}
	}

	//Refreshes the tail of the snake, by removing the superfluous data in positions arraylist
	//and refreshing the display of the things that is removed
	public void deleteTail(){
		int cmpt = sizeSnake;
		for(int i = positions.size()-1;i>=0;i--){
			if(cmpt==0){
			 	// System.out.printf("%d %d\n",positions.get(i).getX() , positions.get(i).getY());
			 	Tuple t = positions.get(i);
			 	state[t.x][t.y] = 0;
				positions.remove(i);
			}
			else{
				if ( cmpt == 1 ) tailSnakePos = positions.get(i);
				cmpt--;
			}
		}
	}
}
package com.gui;

import java.util.ArrayList;

import com.ai.AutoPlay;
import com.gui.*;

public class Snake {
	public Tuple headSnakePos;
	public int sizeSnake=1;
	public int directionSnake;
	public ArrayList<Tuple> positions = new ArrayList<Tuple>();
	public ArrayList<ArrayList<DataOfSquare>> Squares= new ArrayList<ArrayList<DataOfSquare>>();

	public Snake() {
		sizeSnake = 1;
		positions = new ArrayList<Tuple>();
		Squares = Window.Grid;
	}


	//Moves the head of the snake and refreshes the positions in the arraylist
	//1:right 2:left 3:top 4:bottom 0:nothing
	public void moveInterne(int dir){
		switch(dir){
		 	case 4:
				headSnakePos.ChangeData(headSnakePos.x,(headSnakePos.y+1)%20);
				positions.add(new Tuple(headSnakePos.x,headSnakePos.y));
		 		break;
		 	case 3:
		 		if(headSnakePos.y-1<0){
		 			headSnakePos.ChangeData(headSnakePos.x,19);
		 		}
		 		else{
					headSnakePos.ChangeData(headSnakePos.x,Math.abs(headSnakePos.y-1)%20);
		 		}
				positions.add(new Tuple(headSnakePos.x,headSnakePos.y));
		 		break;
		 	case 2:
		 		if(headSnakePos.x-1<0){
		 			headSnakePos.ChangeData(19,headSnakePos.y);
		 		}
		 		else{
		 			headSnakePos.ChangeData(Math.abs(headSnakePos.x-1)%20,headSnakePos.y);
		 		} 
		 		positions.add(new Tuple(headSnakePos.x,headSnakePos.y));

		 		break;
		 	case 1:
				headSnakePos.ChangeData(Math.abs(headSnakePos.x+1)%20,headSnakePos.y);
				positions.add(new Tuple(headSnakePos.x,headSnakePos.y));
		 		break;
		}
		 // System.out.printf("%d %d\n",headSnakePos.getX(),headSnakePos.getY());
	}

	public void turnOn(Tuple u,Tuple v,boolean isHead) {
	 	int uy = u.getX(), ux = u.getY();
	 	int vy = v.getX(), vx = v.getY();

		// System.out.printf("%d %d\n",ux,uy);
		Squares.get(ux).get(uy).lightMeUp(5,2,isHead);

		if ( ux == vx + 1 ) {
		 	Squares.get(ux).get(uy).lightMeUp(1,2,false);
		 	Squares.get(vx).get(vy).lightMeUp(2,2,false);
		} else if ( ux == vx - 1 ) {
		 	Squares.get(ux).get(uy).lightMeUp(2,2,false);
		 	Squares.get(vx).get(vy).lightMeUp(1,2,false);
		} else if ( uy == vy + 1 ) {
		 	Squares.get(ux).get(uy).lightMeUp(3,2,false);
		 	Squares.get(vx).get(vy).lightMeUp(4,2,false);
		} else if ( uy == vy - 1 ) {
		 	Squares.get(ux).get(uy).lightMeUp(4,2,false);
		 	Squares.get(vx).get(vy).lightMeUp(3,2,false);
		}
	}


	//Refresh the squares that needs to be 
	public void moveExterne(){
	 	Tuple prev = new Tuple(-10,-10);

		for(int i=0; i < positions.size(); i++) {
		 	Tuple t = positions.get(i);
		 	boolean isHead = false;
		 	if ( i == positions.size()-1 ) 
		 	 	isHead = true;
			turnOn(t,prev,isHead);
			prev = t;
		}
	}

	//Refreshes the tail of the snake, by removing the superfluous data in positions arraylist
	//and refreshing the display of the things that is removed
	public void deleteTail(){
		int cmpt = sizeSnake;
		for(int i = positions.size()-1;i>=0;i--){
			if(cmpt==0){
				Tuple t = positions.get(i);
				Squares.get(t.y).get(t.x).lightMeUp(5,0,false);
			}
			else {
				cmpt--;
			}
		}
		cmpt = sizeSnake;
		for(int i = positions.size()-1;i>=0;i--){
			if(cmpt==0){
			 	// System.out.printf("%d %d\n",positions.get(i).getX() , positions.get(i).getY());
				positions.remove(i);
			}
			else{
				cmpt--;
			}
		}
	}
}
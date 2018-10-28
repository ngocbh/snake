package com.gui;

import java.util.ArrayList;

import com.ai.AutoPlay;
import com.ai.Utils;
import com.gui.*;

//Controls all the game logic .. most important class in this project.
public class ThreadsController extends Thread {
	ArrayList<ArrayList<DataOfSquare>> Squares= new ArrayList<ArrayList<DataOfSquare>>();
	long speed = 20;
	public boolean autoPlay;

	Tuple foodPosition;
	Snake snake;
	AutoPlay autobot = new AutoPlay();
	//Constructor of ControlleurThread 
	ThreadsController(boolean auto,long spd,Tuple positionDepart){
		//Get all the threads
		autoPlay = auto;
		speed = spd;
		snake = new Snake();
		Squares = Window.Grid;
		snake.headSnakePos=new Tuple(positionDepart.x,positionDepart.y);
		snake.directionSnake = 1;

		//!!! Pointer !!!!
		Tuple headPos = new Tuple(snake.headSnakePos.getX(),snake.headSnakePos.getY());
		snake.positions.add(headPos);
		
		foodPosition= getValAleaNotInSnake();
		spawnFood(foodPosition);

	}
	 
	//Important part :
	public void run() {
		while(true){
		 	System.out.println("-----ROUND---------");
		 	// System.out.println("-----Move Interne---------");
			snake.moveInterne(snake.directionSnake);
			snake.moveExterne();
			// System.out.println("-----Check Collision---------");
			checkCollision();
			// System.out.println("-----Move Externe---------");
			// snake.moveExterne();
			showSnake(snake);
			// System.out.println("-----Delete Tail---------");
			snake.deleteTail();
			pauser();
			System.out.println("-----AI-Snake---------");
			// Utils.printArray(snake.state);
			// snake.tailSnakePos.print();
			// Utils.printArray(Window.convertSimpleGrid(Window.Grid));
			if ( autoPlay ) autoPlaySnake();
			// return;
		}
	}
	 
	 //delay between each move of the snake
	private void pauser(){
		try {
				sleep(speed);
		} catch (InterruptedException e) {
				e.printStackTrace();
		}
	}
	 
	//Checking if the snake bites itself or is eating
	private void checkCollision() {
		Tuple posCritique = snake.positions.get(snake.positions.size()-1);

		int hy = snake.headSnakePos.getX(), hx = snake.headSnakePos.getY();
		if ( Squares.get(hx).get(hy).obj > 1 ) {
		 	stopTheGame();
		}
		 
		boolean eatingFood = posCritique.getX()==foodPosition.y && posCritique.getY()==foodPosition.x;
		if(eatingFood){
			System.out.println("Yummy!");
			snake.sizeSnake=snake.sizeSnake+1;
			 	foodPosition = getValAleaNotInSnake();

			spawnFood(foodPosition);	
		}
	}
	 
	//Stops The Game
	private void stopTheGame(){
		System.out.println("COLISION! \n");
		while(true){
			pauser();
		}
	}
	 
	//Put food in a position and displays it
	private void spawnFood(Tuple foodPositionIn){
		 	Squares.get(foodPositionIn.x).get(foodPositionIn.y).lightMeUp(5,1,false);
	}
	 
	//return a position not occupied by the snake
	private Tuple getValAleaNotInSnake(){
		ArrayList<Tuple> freeSquares = new ArrayList<Tuple>();
		 for (int i = 0; i < Squares.size(); i++)
		 	for (int j = 0; j < Squares.get(i).size(); j++) 
		 		if ( Squares.get(i).get(j).obj == 0 ) 
		 			freeSquares.add(new Tuple(i,j));
		

		int ranVal= 0 + (int)(Math.random()*(freeSquares.size()-1)); 
		return freeSquares.get(ranVal);
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
	public void showSnake(Snake snake){
	 	Tuple prev = new Tuple(-10,-10);

		for(int i=0; i < snake.positions.size(); i++) {
		 	Tuple t = snake.positions.get(i);
		 	boolean isHead = false;
		 	if ( i == snake.positions.size()-1 ) 
		 	 	isHead = true;
			turnOn(t,prev,isHead);
			prev = t;
		}

		//turn off tail
		int cmpt = snake.sizeSnake;
		for(int i = snake.positions.size()-1;i>=0;i--){
			if(cmpt==0){
				Tuple t = snake.positions.get(i);
				Squares.get(t.y).get(t.x).lightMeUp(5,0,false);
			}
			else {
				cmpt--;
			}
		}
	}
	 
	//auto play snake ai
	private void autoPlaySnake() {
	 	autobot.setSnake(snake);
	 	autobot.foodPos = foodPosition;
	 	// System.out.printf("%d %d %d\n",headSnakePos.getY(),headSnakePos.getX(),directionSnake);
	 	snake.directionSnake = autobot.proposeDirection(snake.headSnakePos.getY(),snake.headSnakePos.getX(),snake.directionSnake);
	 	// System.out.printf("%d %d %d\n",headSnakePos.getY(),headSnakePos.getX(),directionSnake);
	}
}

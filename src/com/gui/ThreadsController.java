package com.gui;

import java.util.ArrayList;

import com.ai.AutoPlay;
import com.ai.Utils;
import com.evaluator.IOFile;
import com.gui.*;

//Controls all the game logic .. most important class in this project.
public class ThreadsController extends Thread {
	ArrayList<ArrayList<DataOfSquare>> Squares= new ArrayList<ArrayList<DataOfSquare>>();
	long speed = 20;
	public boolean autoPlay;
	public int algorithm;
	int height;
	int width;
	int step = 0;
	boolean colision = false;
	Tuple foodPosition;
	Snake snake;
	AutoPlay autobot = new AutoPlay();
        GameFrame gameFrame;
        Window window;
        int numOR, currentNumOR;
        double startTime, endTime, time;
        float steps = -1;
        String urlFile;
        IOFile file = new IOFile();
        float score;
	//Constructor of ControlleurThread 
	ThreadsController(boolean auto,long spd,Tuple positionDepart, GameFrame gameFr, Window win, int num, int alg){
		//Get all the threads
		autoPlay = auto;
		speed = spd;
                gameFrame = gameFr;
                window = win;
                algorithm = alg;
                switch(algorithm) {
                    case 1: urlFile = "./src/com/evaluator/data1.txt"; break;
                    case 2: urlFile = "./src/com/evaluator/data2.txt"; break;
                    case 3: urlFile = "./src/com/evaluator/data3.txt"; break;
                    case 4: urlFile = "./src/com/evaluator/data4.txt"; break;
                }
                numOR = num;
		snake = new Snake();

		Squares = Window.Grid;
		height = Squares.size();
		width = Squares.get(0).size();

		snake.headSnakePos=new Tuple(positionDepart.x,positionDepart.y);
		snake.tailSnakePos=new Tuple(positionDepart.x,positionDepart.y);
		snake.directionSnake = 1;

		//!!! Pointer !!!!
		Tuple headPos = new Tuple(snake.headSnakePos.getX(),snake.headSnakePos.getY());
		snake.positions.add(headPos);
		
		foodPosition= getValAleaNotInSnake();
		spawnFood(foodPosition);

	}
	 
	//Important part :
	public void run() {
                startTime = System.currentTimeMillis();
		while(true){
                        
		 	// System.out.println("-----ROUND---------");
		 	// System.out.println("-----Move Interne---------");
			snake.moveInterne(snake.directionSnake);
			// System.out.println("-----Move Externe---------");
			snake.moveExterne();
			// System.out.println("-----Check Collision---------");
			colision = checkCollision();
			// snake.moveExterne();
			showSnake(snake);
			// System.out.println("-----Delete Tail---------");
			snake.deleteTail();

			if (colision == true) {
				stopTheGame();
			}
			else
				pauser();
			// System.out.println("-----AI-Snake---------");
                        steps = steps + 1;
			if ( autoPlay ) autoPlaySnake();
			// return;
			++step;
			// System.out.printf("%d\n",step);
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
	private boolean checkCollision() {
		Tuple posCritique = snake.positions.get(snake.positions.size()-1);

		if ( step > 2*(height * width) )
	 		return true;

		int hx = snake.headSnakePos.getX(), hy = snake.headSnakePos.getY();
		if ( Squares.get(hx).get(hy).obj > 1 ) {
	 		stopTheGame();
		}
		 
		boolean eatingFood = posCritique.getX()==foodPosition.x && posCritique.getY()==foodPosition.y;
		if(eatingFood){
			snake.sizeSnake=snake.sizeSnake+1;
			 	foodPosition = getValAleaNotInSnake();

			spawnFood(foodPosition);	
			step = 0;
		}
		return false;
	}
	 
	//Stops The Game
	private void stopTheGame(){
                endTime = System.currentTimeMillis();
                time = endTime - startTime;
                currentNumOR = Integer.parseInt(file.ReadNumOR(urlFile)) + 1;
                gameFrame.showCurrentAct(currentNumOR,snake.sizeSnake-1,time,steps);
                System.out.println(currentNumOR + " " + (snake.sizeSnake-1) + " " + time + " " +steps);
                if(currentNumOR > 1) {
                    score = ((snake.sizeSnake-1) + Float.parseFloat(file.ReadScore(urlFile))*(currentNumOR-1))/currentNumOR;
                    time = (time + Double.parseDouble(file.ReadTime(urlFile))*(currentNumOR-1))/currentNumOR;
                    steps = (steps + Float.parseFloat(file.ReadStep(urlFile))*(currentNumOR-1))/currentNumOR;
                    file.Write(currentNumOR, score, time, steps, urlFile);
                }
                else
                    file.Write(currentNumOR, snake.sizeSnake-1, time, steps, urlFile);
                
                if(currentNumOR < numOR) {
                    gameFrame.restart(window,speed,algorithm,numOR);
                }
                else
                    gameFrame.getOldWindow(window);
                gameFrame.showResult();
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
		

		if ( freeSquares.size() == 0 ) {
			colision = true;
			return new Tuple(0,0);
		}
		
		int ranVal= 0 + (int)(Math.random()*(freeSquares.size()-1)); 
		return freeSquares.get(ranVal);
	}

	public void turnOn(Tuple u,Tuple v,boolean isHead) {
	 	int ux = u.getX(), uy = u.getY();
	 	int vx = v.getX(), vy = v.getY();

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
				Squares.get(t.x).get(t.y).lightMeUp(5,0,false);
			}
			else {
				cmpt--;
			}
		}
	}
	 
	//auto play snake ai
	private void autoPlaySnake() {
		// snake.state = Window.convertSimpleGrid(Window.Grid);
	 	autobot.setSnake(snake);
	 	autobot.foodPos = foodPosition;
	 	autobot.algorithm = algorithm;
	 	// System.out.printf("%d %d %d\n",headSnakePos.getY(),headSnakePos.getX(),directionSnake);
	 	snake.directionSnake = autobot.proposeDirection(snake,snake.directionSnake);
	 	// System.out.printf("%d %d %d\n",headSnakePos.getY(),headSnakePos.getX(),directionSnake);
	}
}

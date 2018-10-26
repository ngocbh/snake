package com.gui;

import java.util.ArrayList;

import com.ai.AutoPlay;
import com.gui.*;

//Controls all the game logic .. most important class in this project.
public class ThreadsController extends Thread {
	 ArrayList<ArrayList<DataOfSquare>> Squares= new ArrayList<ArrayList<DataOfSquare>>();
	 Tuple headSnakePos;
	 int sizeSnake=1;
	 long speed = 20;
	 public static int directionSnake ;
	 public static boolean autoPlay = true;
	 ArrayList<Tuple> positions = new ArrayList<Tuple>();
	 Tuple foodPosition;
	 AutoPlay brain;

	 //Constructor of ControlleurThread 
	 ThreadsController(Tuple positionDepart){
		//Get all the threads
		Squares=Window.Grid;
		brain = new AutoPlay(Window.convertSimpleGrid(Window.Grid));
		headSnakePos=new Tuple(positionDepart.x,positionDepart.y);
		directionSnake = 1;

		//!!! Pointer !!!!
		Tuple headPos = new Tuple(headSnakePos.getX(),headSnakePos.getY());
		positions.add(headPos);
		
		foodPosition= getValAleaNotInSnake();
		spawnFood(foodPosition);

	 }
	 
	 //Important part :
	 public void run() {
		 while(true){
		 	System.out.println("-----ROUND---------");
		 	// System.out.printf("%d %d\n",headSnakePos.getX(),headSnakePos.getY());
		 	// System.out.println("-----AI-Snake---------");
		 	
		 	// System.out.println("-----Move Interne---------");
			moveInterne(directionSnake);
			// System.out.println("-----Check Collision---------");
			checkCollision();
			// System.out.println("-----Move Externe---------");
			moveExterne();
			// System.out.println("-----Delete Tail---------");
			deleteTail();
			pauser();
			
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
		 Tuple posCritique = positions.get(positions.size()-1);

		 int hy = headSnakePos.getX(), hx = headSnakePos.getY();
		 if ( Squares.get(hx).get(hy).obj > 1 ) {
		 	stopTheGame();
		 }
		 
		 boolean eatingFood = posCritique.getX()==foodPosition.y && posCritique.getY()==foodPosition.x;
		 if(eatingFood){
			 System.out.println("Yummy!");
			 sizeSnake=sizeSnake+1;
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
	 
	 //Moves the head of the snake and refreshes the positions in the arraylist
	 //1:right 2:left 3:top 4:bottom 0:nothing
	 private void moveInterne(int dir){
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

	 private void turnOn(Tuple u,Tuple v,boolean isHead) {
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
	 private void moveExterne(){
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
	 private void deleteTail(){
		 int cmpt = sizeSnake;
		 for(int i = positions.size()-1;i>=0;i--){
			 if(cmpt==0){
				 Tuple t = positions.get(i);
				 Squares.get(t.y).get(t.x).lightMeUp(5,0,false);
			 }
			 else{
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

	 //auto play snake ai
	 private void autoPlaySnake() {
	 	AutoPlay.setState(Window.convertSimpleGrid(Window.Grid));
	 	AutoPlay.setSnake(positions);
	 	AutoPlay.setHeadSnake(headSnakePos);
	 	AutoPlay.setSizeSnake(sizeSnake);
	 	// System.out.printf("%d %d %d\n",headSnakePos.getY(),headSnakePos.getX(),directionSnake);
	 	directionSnake = AutoPlay.proposeDirection(headSnakePos.getY(),headSnakePos.getX(),directionSnake);
	 	// System.out.printf("%d %d %d\n",headSnakePos.getY(),headSnakePos.getX(),directionSnake);
	 }
}

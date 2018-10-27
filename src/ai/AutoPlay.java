package com.ai;

import java.util.ArrayList;
import java.util.LinkedList; 
import java.util.Queue; 
import java.util.Stack;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.gui.*;

public class AutoPlay {
	public int[][] state;
	public ArrayList<Tuple> snake;
	public Tuple headSnake;
	public int sizeSnake = 1;

	public int[][] predictState;
	public Tuple predictHeadSnake;
	public ArrayList<Tuple> predictSnake;

	public Tuple foodPos;
	public Stack<Tuple> path;
	public int proposal = 0;

	public AutoPlay() {} 

	public AutoPlay(int h,int c) {
		this.state = new int[h][c];
	}

	public AutoPlay(int[][] stt) {
		this.state = stt;
	}

	public void setState(int[][] stt){
		this.state = stt;
	}

	public void setSnake(ArrayList<Tuple> ssnake) {
		this.snake = ssnake;
	} 

	public void setHeadSnake(Tuple hheadSnake) {
		this.headSnake = hheadSnake;
	}

	public void setSizeSnake(int size) {
		this.sizeSnake = size;
	}

	public void setFoodPos(Tuple foodPosition) {
		this.foodPos = foodPosition;
	}

	//--------------------------------------------------------------
	// BFS proposal + Wander
	//--------------------------------------------------------------
	int BFSProposal(int hx,int hy,int oldDir) {
		if (path == null || path.size() == 0 ) 	
			path = Utils.findShortestPath(state,hx,hy,foodPos.x,foodPos.y);

		if ( path.size() == 0 ) {
			path = null;
			if ( state[hx-1][hy] < 2 ) return 3;
			if ( state[hx+1][hy] < 2 ) return 4;
			if ( state[hx][hy-1] < 2 ) return 2;
			if ( state[hx][hy+1] < 2 ) return 1;
		} else {
			Tuple direct = path.pop();
			return direct.dir;
		}
		return oldDir;
	}

	/*
	* source : https://github.com/chuyangliu/Snake/blob/master/docs/algorithms.md
	* Greedy Solver
	* Greedy Solver directs the snake to eat the food along the shortest path if it thinks the snake will be safe. 
	* Otherwise, it makes the snake wander around until a safe path can be found. 
	* As it needs paths searching, it depends on Path Solver.
	* Concretely, to find the snake S1's next moving direction D, the solver follows the steps below:
	* 1. Compute the shortest path P1 from S1's head to the food. If P1 exists, go to step 2. Otherwise, go to step 4.
	* 2. Move a virtual snake S2 (the same as S1) to eat the food along path P1.
	* 3. Compute the longest path P2 from S2's head to its tail. If P2 exists, let D be the first direction in path P1. Otherwise, go to step 4.
	* 4. Compute the longest path P3 from S1's head to its tail. If P3 exists, let D be the first direction in path P3. Otherwise, go to step 5.
	* 5. Let D be the direction that makes S1 the farthest from the food.
	*/
	int  GreedyAlgorithm(int hx,int hy,int oldDir) {
		if (path == null || path.size() == 0 ) 	
			path = Utils.findShortestPath(state,hx,hy,foodPos.x,foodPos.y);

		if ( path.size() == 0 ) {
			path = null;
			if ( state[hx-1][hy] < 2 ) return 3;
			if ( state[hx+1][hy] < 2 ) return 4;
			if ( state[hx][hy-1] < 2 ) return 2;
			if ( state[hx][hy+1] < 2 ) return 1;
		} else {
			Tuple direct = path.pop();
			return direct.dir;
		}
		return oldDir;
	}

	public int proposeDirection(int hx,int hy,int oldDir) {

		return BFSProposal(hx,hy,oldDir);
	}

}
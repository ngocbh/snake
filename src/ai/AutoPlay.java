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
	public Snake snake;

	public LinkedList<Tuple> path = new LinkedList<Tuple>();
	public Tuple foodPos;
	public int proposal = 0;

	public AutoPlay() {} 

	public void setSnake(Snake ssnake) {
		this.snake = ssnake;
	} 

	public void setFoodPos(Tuple foodPosition) {
		this.foodPos = foodPosition;
	}

	public static Snake cloneSnake(Snake snake) {
		Snake res = new Snake();
		res.headSnakePos = new Tuple(snake.headSnakePos.x,snake.headSnakePos.y);
		res.tailSnakePos = new Tuple(snake.tailSnakePos.x,snake.tailSnakePos.y);
		res.sizeSnake = snake.sizeSnake;
		res.directionSnake = snake.directionSnake;
		res.positions = (ArrayList<Tuple>) snake.positions.clone();
		res.state = Utils.copyArray(snake.state);
		return res;
	}

	//--------------------------------------------------------------
	// BFS proposal + Wander
	//--------------------------------------------------------------
	int BFSProposal(Snake snake,int oldDir) {
		int hx = snake.headSnakePos.x;
		int hy = snake.headSnakePos.y;

		if (path == null || path.size() == 0 ) 	
			path = Utils.findShortestPath(snake.state,hx,hy,foodPos.x,foodPos.y);

		if ( path.size() == 0 ) {
			path = null;
			if ( snake.state[hx-1][hy] < 2 ) return 3;
			if ( snake.state[hx+1][hy] < 2 ) return 4;
			if ( snake.state[hx][hy-1] < 2 ) return 2;
			if ( snake.state[hx][hy+1] < 2 ) return 1;
		} else {
			Tuple direct = path.remove();
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
	int  GreedyAlgorithm(Snake snake,int oldDir) {
		//step 1
		if (path != null && path.size() > 0 ) {
			Tuple direct = path.remove();
			return direct.dir;
		}

		LinkedList<Tuple> longestPath, shortestPath;
		int hx = snake.headSnakePos.x;
		int hy = snake.headSnakePos.y;

		shortestPath = Utils.findShortestPath(snake.state,hx,hy,foodPos.x,foodPos.y);
		

		//step 2
		if ( shortestPath.size() > 0 ) {
			Snake virtualSnake = cloneSnake(snake);
			//virtual run 
			for (int i = 0; i < shortestPath.size(); i++) {
				virtualSnake.directionSnake = shortestPath.get(i).dir;
				virtualSnake.moveInterne(virtualSnake.directionSnake);
				virtualSnake.moveExterne();
				virtualSnake.deleteTail();
			}

			//step 3
			longestPath = Utils.findLongestPath(virtualSnake.state,virtualSnake.headSnakePos.x,virtualSnake.headSnakePos.y,virtualSnake.tailSnakePos.x,virtualSnake.tailSnakePos.y);

			if ( longestPath.size() > 0 || ( virtualSnake.headSnakePos.x == virtualSnake.tailSnakePos.x && virtualSnake.headSnakePos.y == virtualSnake.tailSnakePos.y ) ) {
				path = (LinkedList<Tuple>) shortestPath.clone();
				Tuple direct = path.remove();
				return direct.dir;
			}
		}

		//step 4
		longestPath = Utils.findLongestPath(snake.state,hx,hy,snake.tailSnakePos.x,snake.tailSnakePos.y);
		if (longestPath.size() > 0) {
			Tuple direct = longestPath.remove();
			return direct.dir;
		}

		//step 4
		if ( snake.state[hx-1][hy] < 2 ) return 3;
		if ( snake.state[hx+1][hy] < 2 ) return 4;
		if ( snake.state[hx][hy-1] < 2 ) return 2;
		if ( snake.state[hx][hy+1] < 2 ) return 1;

		return 1;
	}

	public int proposeDirection(Snake snake,int oldDir) {

		if ( Main.algorithm == 1 )
			return BFSProposal(snake,oldDir);
		else if ( Main.algorithm == 2 ) 
			return GreedyAlgorithm(snake,oldDir);
		return oldDir;
	}

}
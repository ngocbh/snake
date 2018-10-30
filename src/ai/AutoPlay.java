package com.ai;

import java.util.ArrayList;
import java.util.LinkedList; 
import java.util.Queue; 
import java.util.Stack;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.gui.*;
import com.gui.Window;

public class AutoPlay {
	public Snake snake;
	public int algorithm = 4;
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

	//--------------------------------------------------------------
	// GreedyAlgorithm base on BFS to find shortest path
	//--------------------------------------------------------------
	int  GreedyAlgorithm(Snake snake,int oldDir) {
		//step 1
		// if (path != null && path.size() > 0 ) {
		// 	Tuple direct = path.remove();
		// 	return direct.dir;
		// }

		// foodPos.print();

		LinkedList<Tuple> longestPath, shortestPath;
		int hx = snake.headSnakePos.x;
		int hy = snake.headSnakePos.y;

		shortestPath = Utils.findShortestPath(snake.state,hx,hy,foodPos.x,foodPos.y);
	
		//step 2
		if ( shortestPath.size() > 0 ) {
			Snake virtualSnake = cloneSnake(snake);
			virtualSnake.sizeSnake += 1;
			//virtual run 
			for (int i = 0; i < shortestPath.size(); i++) {
				virtualSnake.directionSnake = shortestPath.get(i).dir;
				virtualSnake.moveInterne(virtualSnake.directionSnake);
				virtualSnake.moveExterne();
				virtualSnake.deleteTail();
			}

			//step 3
			longestPath = Utils.findLongestPath(virtualSnake.state,virtualSnake.headSnakePos.x,virtualSnake.headSnakePos.y,virtualSnake.tailSnakePos.x,virtualSnake.tailSnakePos.y);

			if ( longestPath.size() > 1 || ( virtualSnake.sizeSnake < 3 ) ) {
				// path = (LinkedList<Tuple>) shortestPath.clone();
				// Tuple direct = path.remove();
				// System.out.println("=======step 3========");
				Tuple direct = shortestPath.remove();
				return direct.dir;
			}
		}

		//step 4
		longestPath = Utils.findLongestPath(snake.state,hx,hy,snake.tailSnakePos.x,snake.tailSnakePos.y);

		if (longestPath.size() > 1) {
			Tuple direct = longestPath.remove();
			// System.out.println("=======step 4========");
			return direct.dir;
		}

		//step 5
		longestPath = Utils.findLongestPath(snake.state,hx,hy,foodPos.x,foodPos.y);
		if (longestPath.size() > 1) {
			Tuple direct = longestPath.remove();
			// System.out.println("=======step 5========");
			return direct.dir;
		}

		//step 6 : Wander 1
		// System.out.println("=======step 6========");
		if ( hx > 1 &&  snake.state[hx-2][hy] < 2 && snake.state[hx-1][hy] < 2 ) return 3;
		if ( hx < snake.state.length-1 &&  snake.state[hx+2][hy] < 2 && snake.state[hx+1][hy] < 2 ) return 4;
		if ( hy > 1 &&  snake.state[hx][hy-2] < 2 && snake.state[hx][hy-1] < 2 ) return 2;
		if ( hy < snake.state[0].length-1 &&  snake.state[hx][hy+2] < 2 &&snake.state[hx][hy+1] < 2 ) return 1;

		//step 7 : Wander 2
		// System.out.println("=======step 7========");
		if ( snake.state[hx-1][hy] < 2 ) return 3;
		if ( snake.state[hx+1][hy] < 2 ) return 4;
		if ( snake.state[hx][hy-1] < 2 ) return 2;
		if ( snake.state[hx][hy+1] < 2 ) return 1;

		return oldDir;
	}

	//--------------------------------------------------------------
	// a start proposal
	//--------------------------------------------------------------
	int A_Star_Proposal(Snake snake,int oldDir) {
		int hx = snake.headSnakePos.x;
		int hy = snake.headSnakePos.y;

		// if (path == null || path.size() == 0 ) 	
		path = Utils.findShortestPath_a_star(snake.state,hx,hy,foodPos.x,foodPos.y);

		if ( path.size() == 0 ) {
			path = null;
			// System.out.println("=======path null========");
			if ( snake.state[hx-1][hy] < 2 ) return 3;
			if ( snake.state[hx+1][hy] < 2 ) return 4;
			if ( snake.state[hx][hy-1] < 2 ) return 2;
			if ( snake.state[hx][hy+1] < 2 ) return 1;
		} else {
			// System.out.println("=======path not null========");
			Tuple direct = path.remove();
			return direct.dir;
		}
		return oldDir;
	}


	int  GreedyAlgorithm_a_star(Snake snake,int oldDir) {
		//step 1
		// if (path != null && path.size() > 0 ) {
		// 	Tuple direct = path.remove();
		// 	return direct.dir;
		// }

		LinkedList<Tuple> longestPath, shortestPath;
		int hx = snake.headSnakePos.x;
		int hy = snake.headSnakePos.y;

		shortestPath = Utils.findShortestPath_a_star(snake.state,hx,hy,foodPos.x,foodPos.y);

		//step 2
		if ( shortestPath.size() > 0 ) {
			Snake virtualSnake = cloneSnake(snake);
			virtualSnake.sizeSnake += 1;
			// //virtual run 
			// System.out.println("=======step 2========");
			for (int i = 0; i < shortestPath.size(); i++) {
				virtualSnake.directionSnake = shortestPath.get(i).dir;
				virtualSnake.moveInterne(virtualSnake.directionSnake);
				virtualSnake.moveExterne();
				virtualSnake.deleteTail();
			}

			//step 3
			longestPath = Utils.findLongestPath_a_star(virtualSnake.state,virtualSnake.headSnakePos.x,virtualSnake.headSnakePos.y,virtualSnake.tailSnakePos.x,virtualSnake.tailSnakePos.y);

			if ( longestPath.size() > 1 || virtualSnake.sizeSnake <= 2 ) {
				// path = (LinkedList<Tuple>) shortestPath.clone();
				// Tuple direct = path.remove();
				// System.out.println("=======step 3 ========");
				Tuple direct = shortestPath.remove();
				return direct.dir;
			}

			
		}

		//step 4
		longestPath = Utils.findLongestPath_a_star(snake.state,hx,hy,snake.tailSnakePos.x,snake.tailSnakePos.y);
		if (longestPath.size() > 1) {
			Tuple direct = longestPath.remove();
			// System.out.println("=======step 4========");
			return direct.dir;
		}

		//step 5
		longestPath = Utils.findLongestPath_a_star(snake.state,hx,hy,foodPos.x,foodPos.y);
		if (longestPath.size() > 1) {
			Tuple direct = longestPath.remove();
			// System.out.println("=======step 5========");
			return direct.dir;
		}

		//step 6 : Wander 1
		// System.out.println("=======step 6========");
		if ( hx > 1 &&  snake.state[hx-2][hy] < 2 && snake.state[hx-1][hy] < 2 ) return 3;
		if ( hx < snake.state.length-1 &&  snake.state[hx+2][hy] < 2 && snake.state[hx+1][hy] < 2 ) return 4;
		if ( hy > 1 &&  snake.state[hx][hy-2] < 2 && snake.state[hx][hy-1] < 2 ) return 2;
		if ( hy < snake.state[0].length-1 &&  snake.state[hx][hy+2] < 2 &&snake.state[hx][hy+1] < 2 ) return 1;

		//step 7 : Wander 2
		// System.out.println("=======step 7========");
		if ( snake.state[hx-1][hy] < 2 ) return 3;
		if ( snake.state[hx+1][hy] < 2 ) return 4;
		if ( snake.state[hx][hy-1] < 2 ) return 2;
		if ( snake.state[hx][hy+1] < 2 ) return 1;

		return oldDir;
	}

	public int proposeDirection(Snake snake,int oldDir) {
		if ( algorithm == 1 )
			return BFSProposal(snake,oldDir);
		else if ( algorithm == 2 ) 
			return GreedyAlgorithm(snake,oldDir);
		else if ( algorithm == 3 ) 
			return A_Star_Proposal(snake,oldDir);
		else if ( algorithm == 4 ) 
			return GreedyAlgorithm_a_star(snake,oldDir);
		return oldDir;
	}

}
package com.ai;

import java.util.ArrayList;
import java.util.LinkedList; 
import java.util.Stack; 
import java.util.Queue;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Iterator;

import com.gui.Tuple;

public class Utils {

	public static void printArray(int[][] arr) {
		System.out.println("====Array Print====");
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[0].length; j++)
				System.out.printf("%d",arr[i][j]);
			System.out.println("");
		}
	}

	public static int[][] copyArray(int[][] arr) {
		int[][] res = new int[arr.length][arr[0].length];
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++)
				res[i][j] = arr[i][j];
		}
		return res;
	}

	//--------------------------------------------------------------
	// use bfs to find shortest path
	//--------------------------------------------------------------
	public static LinkedList<Tuple> findShortestPath(int[][] state,int sx,int sy,int dx,int dy) {
		int h = state.length, w = state[0].length;
		Tuple u, v;
		int[][] ca = copyArray(state);
		Tuple[][] trace = new Tuple[h][w];
		Queue<Tuple> qu = new LinkedList<Tuple>();
		
		Tuple sour = new Tuple(sx,sy);
		for (int d = 1; d <= 4; d++) {
			v = sour.next(d); 
			if ( state[v.x][v.y] == 0 ) { qu.add(v); ca[v.x][v.y] = 5; trace[v.x][v.y] = sour; }
		}

		while (qu.size() > 0) {
			u = qu.poll();

			if ( u.x == dx && u.y == dy ) {
				LinkedList<Tuple> res = new LinkedList<Tuple>();
				while (u.x != sx || u.y != sy ) {
					res.add(u);
					u = trace[u.x][u.y];
				}
				Collections.reverse(res);
				return res;
			}

			if ( state[u.x][u.y] != 0 ) continue;

			for (int d = 1; d <= 4; d++) {
				v = u.next(d);
				if ( ca[v.x][v.y] < 5) {
					qu.add(v);
					ca[v.x][v.y] = 5;
					trace[v.x][v.y] = u;
				}
			}
		}

		return new LinkedList<Tuple>();
	}


	//--------------------------------------------------------------
	// find the longest path 
	//--------------------------------------------------------------
	public static LinkedList<Tuple> findLongestPath(int[][] state,int sx,int sy,int dx,int dy) {
		LinkedList<Tuple> longestPath = findShortestPath_a_star(state,sx,sy,dx,dy);
		int[][] ca = copyArray(state);
	
		if ( longestPath.size() == 0 ) return longestPath;

		for (int i = 0; i < longestPath.size(); i++) {
			ca[longestPath.get(i).x][longestPath.get(i).y] = 5;
		}

		//explose path
		int i = 0;
		while ( true ) {
			if ( i >= longestPath.size() ) break;

			Tuple v = longestPath.get(i);
			int dir = v.dir;
			Tuple u = v.prev(dir);

			Tuple ul = u.left(), vl = v.left();
			if ( ca[ul.x][ul.y] == 0 && ca[vl.x][vl.y] == 0 ) {
				ca[ul.x][ul.y] = 5;
				ca[vl.x][vl.y] = 5;
				longestPath.add(i,ul);
				vl.dir = dir;
				longestPath.add(i+1,vl);
				longestPath.get(i+2).dir = Tuple.opposite(ul.dir);
				continue;
			}
			
			Tuple ur = u.right(), vr = v.right();
			if ( ca[ur.x][ur.y] == 0 && ca[vr.x][vr.y] == 0 ) {
				ca[ur.x][ur.y] = 5;
				ca[vr.x][vr.y] = 5;
				longestPath.add(i,ur);
				vr.dir = dir;
				longestPath.add(i+1,vr);
				longestPath.get(i+2).dir = Tuple.opposite(ur.dir);
			}

			i++;
		}

		// System.out.println("======Longest Path=========");
		// for (i = 0; i < longestPath.size(); i++) 
		// 	longestPath.get(i).print();

		return longestPath;
	}


	//--------------------------------------------------------------
	// next and calculate heuristic function
	//--------------------------------------------------------------
	static Tuple next(Tuple cur,int dir,Tuple dest) {
		if (dir == 1) 
			return new Tuple(cur.x,cur.y+1,dir,cur.length+1,dest);
		else if (dir == 2) 
			return new Tuple(cur.x,cur.y-1,dir,cur.length+1,dest);
		else if (dir == 3)
			return new Tuple(cur.x-1,cur.y,dir,cur.length+1,dest);
		return new Tuple(cur.x+1,cur.y,dir,cur.length+1,dest);
	}

	//--------------------------------------------------------------
	// use a star to find shortest path : heuristic_cost_estimate = abs(dx-ux) + abs(dy-uy)
	//--------------------------------------------------------------
	public static LinkedList<Tuple> findShortestPath_a_star(int[][] state,int sx,int sy,int dx,int dy) {
		int h = state.length, w = state[0].length;
		Tuple u, v;
		int[][] ca = copyArray(state);
		Tuple[][] trace = new Tuple[h][w];
		PriorityQueue<Tuple> qu = new PriorityQueue<Tuple>(Tuple.tupleComparator());
		
		Tuple sour = new Tuple(sx,sy,0,0,0);
		Tuple dest = new Tuple(dx,dy,0,0,0);
		if ( sx == dx && sy == dy ) return new LinkedList<Tuple>();


		for (int d = 1; d <= 4; d++) {
			v = next(sour,d,dest); 
			if ( state[v.x][v.y] == 0 ) { qu.add(v); ca[v.x][v.y] = 5; trace[v.x][v.y] = sour; }
		}
	
		while (qu.size() > 0) {
			u = qu.remove();
			if ( u.x == dx && u.y == dy ) {
				LinkedList<Tuple> res = new LinkedList<Tuple>();
				while (u.x != sx || u.y != sy ) {
					res.add(u);
					u = trace[u.x][u.y];
				}
				Collections.reverse(res);
				return res;
			}

			if ( state[u.x][u.y] != 0 ) continue;

			for (int d = 1; d <= 4; d++) {
				v = next(u,d,dest);
				if ( ca[v.x][v.y] < 5) {
					qu.add(v);
					ca[v.x][v.y] = 5;
					trace[v.x][v.y] = u;
				}
			}
		}

		return new LinkedList<Tuple>();
	}


	//--------------------------------------------------------------
	// find the longest path 
	//--------------------------------------------------------------
	public static LinkedList<Tuple> findLongestPath_a_star(int[][] state,int sx,int sy,int dx,int dy) {
		LinkedList<Tuple> longestPath = findShortestPath_a_star(state,sx,sy,dx,dy);
		int[][] ca = copyArray(state);

		if ( longestPath.size() == 0 ) return longestPath;

		for (int i = 0; i < longestPath.size(); i++) {
			ca[longestPath.get(i).x][longestPath.get(i).y] = 5;
		}

		//explose path
		int i = 0;
		while ( true ) {
			if ( i >= longestPath.size() ) break;

			Tuple v = longestPath.get(i);
			int dir = v.dir;
			Tuple u = v.prev(dir);

			Tuple ul = u.left(), vl = v.left();
			if ( ca[ul.x][ul.y] == 0 && ca[vl.x][vl.y] == 0 ) {
				ca[ul.x][ul.y] = 5;
				ca[vl.x][vl.y] = 5;
				longestPath.add(i,ul);
				vl.dir = dir;
				longestPath.add(i+1,vl);
				longestPath.get(i+2).dir = Tuple.opposite(ul.dir);
				continue;
			}
			
			Tuple ur = u.right(), vr = v.right();
			if ( ca[ur.x][ur.y] == 0 && ca[vr.x][vr.y] == 0 ) {
				ca[ur.x][ur.y] = 5;
				ca[vr.x][vr.y] = 5;
				longestPath.add(i,ur);
				vr.dir = dir;
				longestPath.add(i+1,vr);
				longestPath.get(i+2).dir = Tuple.opposite(ur.dir);
			}

			i++;
		}

		return longestPath;
	}
}
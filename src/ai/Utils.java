package com.ai;

import java.util.ArrayList;
import java.util.LinkedList; 
import java.util.Stack; 
import java.util.Queue;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.gui.Tuple;

public class Utils {

	public static void printArray(int[][] arr) {
		System.out.println("====Array Print====");
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr.length; j++)
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

	public static Tuple next(Tuple a,int dir) {
		if (dir == 1) 
			return new Tuple(a.x,a.y+1,dir);
		else if (dir == 2) 
			return new Tuple(a.x,a.y-1,dir);
		else if (dir == 3)
			return new Tuple(a.x-1,a.y,dir);
		return new Tuple(a.x+1,a.y,dir);
	}

	public static Tuple prev(Tuple a,int dir) {
		if (dir == 1)
			return new Tuple(a.x,a.y-1,dir);
		else if (dir == 2)
			return new Tuple(a.x,a.y+1,dir);
		else if (dir == 3) 
			return new Tuple(a.x+1,a.y,dir);
		return new Tuple(a.x-1,a.y,dir);
	}


	//--------------------------------------------------------------
	// use bfs to find shotest path
	//--------------------------------------------------------------
	public static Stack<Tuple> findShortestPath(int[][] state,int sx,int sy,int dx,int dy) {
		int h = state.length, w = state[0].length;
		Tuple u, v;
		int[][] ca = copyArray(state);
		Tuple[][] trace = new Tuple[h][w];
		Queue<Tuple> qu = new LinkedList<Tuple>();
		
		u = new Tuple(sx,sy);
		v = next(u,1); 
		if ( state[v.x][v.y] == 0 ) { qu.add(v); trace[v.x][v.y] = u; }
		v = next(u,2); 
		if ( state[v.x][v.y] == 0 ) { qu.add(v); trace[v.x][v.y] = u; }
		v = next(u,3); 
		if ( state[v.x][v.y] == 0 ) { qu.add(v); trace[v.x][v.y] = u; }
		v = next(u,4); 
		if ( state[v.x][v.y] == 0 ) { qu.add(v); trace[v.x][v.y] = u; }

		while (qu.size() > 0) {
			u = qu.poll();

			if ( u.x == dx && u.y == dy ) {
				Stack<Tuple> res = new Stack<Tuple>();
				while (u.x != sx || u.y != sy ) {
					res.push(u);
					u = trace[u.x][u.y];
				}
				return res;
			}

			if ( ca[u.x][u.y] != 0 ) continue;
			ca[u.x][u.y] = 5;

			v = next(u,1);
			if ( ca[v.x][v.y] != 5 ) {
				qu.add(v);
				if ( trace[v.x][v.y] == null ) trace[v.x][v.y] = u;
			}
			v = next(u,2);
			if ( ca[v.x][v.y] != 5 ) {
				qu.add(v);
				if ( trace[v.x][v.y] == null ) trace[v.x][v.y] = u;
			}
			v = next(u,3);
			if ( ca[v.x][v.y] != 5 ) {
				qu.add(v);
				if ( trace[v.x][v.y] == null ) trace[v.x][v.y] = u;
			}
			v = next(u,4);
			if ( ca[v.x][v.y] != 5 ) {
				qu.add(v);
				if ( trace[v.x][v.y] == null ) trace[v.x][v.y] = u;
			}
		}

		return new Stack<Tuple>();
	}

	//--------------------------------------------------------------
	// find the longest path 
	//--------------------------------------------------------------
	public static void findLongestPath(int state,int sx,int sy,int dx,int dy) {
		
	}
}
package com.ai;

import java.util.ArrayList;
import java.util.LinkedList; 
import java.util.Stack; 
import java.util.Queue;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ListIterator;

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
		
		u = new Tuple(sx,sy);
		v = u.next(1); 
		if ( state[v.x][v.y] == 0 ) { qu.add(v); trace[v.x][v.y] = u; }
		v = u.next(2); 
		if ( state[v.x][v.y] == 0 ) { qu.add(v); trace[v.x][v.y] = u; }
		v = u.next(3); 
		if ( state[v.x][v.y] == 0 ) { qu.add(v); trace[v.x][v.y] = u; }
		v = u.next(4); 
		if ( state[v.x][v.y] == 0 ) { qu.add(v); trace[v.x][v.y] = u; }

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

			if ( ca[u.x][u.y] != 0 ) continue;
			ca[u.x][u.y] = 5;

			v = u.next(1);
			if ( ca[v.x][v.y] != 5 ) {
				qu.add(v);
				if ( trace[v.x][v.y] == null ) trace[v.x][v.y] = u;
			}
			v = u.next(2);
			if ( ca[v.x][v.y] != 5 ) {
				qu.add(v);
				if ( trace[v.x][v.y] == null ) trace[v.x][v.y] = u;
			}
			v = u.next(3);
			if ( ca[v.x][v.y] != 5 ) {
				qu.add(v);
				if ( trace[v.x][v.y] == null ) trace[v.x][v.y] = u;
			}
			v = u.next(4);
			if ( ca[v.x][v.y] != 5 ) {
				qu.add(v);
				if ( trace[v.x][v.y] == null ) trace[v.x][v.y] = u;
			}
		}

		return new LinkedList<Tuple>();
	}

	//--------------------------------------------------------------
	// find the longest path 
	//--------------------------------------------------------------
	public static LinkedList<Tuple> findLongestPath(int[][] state,int sx,int sy,int dx,int dy) {
		LinkedList<Tuple> longestPath = findShortestPath(state,sx,sy,dx,dy);
		int[][] ca = copyArray(state);

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
}
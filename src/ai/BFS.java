package com.ai;

import java.util.ArrayList;
import java.util.LinkedList; 
import java.util.Queue; 
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.gui.*;

public class BFS {
	//--------------------------------------------------------------
 	// BFS ---- it will return shortest path
 	//--------------------------------------------------------------
	public static int bfs(int[][] state,int hx,int hy,int dir,int dest) {
		Queue<Tuple> qu = new LinkedList<Tuple>();
		int[][] ca = AutoPlay.copyArray(state);
		// qu.add(new Tuple(hx,hy,oldDir));
		// ca[hx][hy] = 3;
		// printArray(ca);
		int ux = hx, uy = hy;
		if ( ca[ux-1][uy] < 2 && dir == 3 ) {
			ca[ux-1][uy] = dir + 4;
			qu.add(new Tuple(ux-1,uy,0));
		}
		if ( ca[ux+1][uy] < 2 && dir == 4 ) {
			ca[ux+1][uy] = dir + 4;
			qu.add(new Tuple(ux+1,uy,0));
		}
		if ( ca[ux][uy-1] < 2 && dir == 2 ) {
			ca[ux][uy-1] = dir + 4;
			qu.add(new Tuple(ux,uy-1,0));
		}
		if ( ca[ux][uy+1] < 2 && dir == 1 ) {
			ca[ux][uy+1] = dir + 4;
			qu.add(new Tuple(ux,uy+1,0));
		}

		while (qu.size() > 0) {
			Tuple u = qu.poll();
			ux = u.getX();
			uy = u.getY();
			int length = u.getLength();
			// System.out.printf("%d %d %d\n",ux,uy,length);

			if ( state[ux][uy] == dest ) {
				//return proposal;
				// printArray(ca);
				return length;
			}

			if ( ca[ux-1][uy] < 2 ) {
				ca[ux-1][uy] = dir + 4;
				qu.add(new Tuple(ux-1,uy,length+1));
			}
			if ( ca[ux+1][uy] < 2 ) {
				ca[ux+1][uy] = dir + 4;
				qu.add(new Tuple(ux+1,uy,length+1));
			}
			if ( ca[ux][uy-1] < 2 ) {
				ca[ux][uy-1] = dir + 4;
				qu.add(new Tuple(ux,uy-1,length+1));
			}
			if ( ca[ux][uy+1] < 2 ) {
				ca[ux][uy+1] = dir + 4;
				qu.add(new Tuple(ux,uy+1,length+1));
			}
		}
		return -1;
	}

	//--------------------------------------------------------------
	// BFS proposal --- 
	//--------------------------------------------------------------
	public static int bfsProposal(int[][] state,int hx,int hy,int oldDir) {
		ArrayList<Tuple> res = new ArrayList<Tuple>();

		for (int i = 1; i <= 4; i++) { 
			if ( (oldDir == 4 && i == 3 ) || ( oldDir == 3 && i == 4 ) ) continue;
			if ( (oldDir == 1 && i == 2 ) || ( oldDir == 2 && i == 1 ) ) continue;
			int length = bfs(state,hx,hy,i,1);
			if ( length != -1 ) 
				res.add(new Tuple(i,length));
			System.out.printf("%d %d\n",i,length);
		}

		Collections.sort(res, new Comparator<Tuple>() {
	        @Override
	        public int compare(Tuple u, Tuple v)
	        {
	        	return u.y - v.y;
	        }
	    });

		for (int i = 0; i < res.size(); i++) {
			System.out.printf("%d %d\n",res.get(i).getX(),res.get(i).getY());
			int dir = res.get(i).getX();
			int length = res.get(i).getY();
			// if ( bfs(    ))
			return dir;
		}
	    return 0;
	}
}
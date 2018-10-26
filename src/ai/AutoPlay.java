package ai;

import java.util.ArrayList;
import java.util.LinkedList; 
import java.util.Queue; 
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.gui.*;
//AutoPlay Snake
public class AutoPlay {
	static int[][] state;
	static ArrayList<Tuple> snake;
	static Tuple headSnake;
	static int sizeSnake = 1;

	static int[][] predictState;
	static Tuple predictHeadSnake;
	static ArrayList<Tuple> predictSnake;

	static int proposal = 0;

	public AutoPlay() {} 

	public AutoPlay(int h,int c) {
		state = new int[h][c];
	}

	public AutoPlay(int[][] stt) {
		state = stt;
	}

	public static void setState(int[][] stt){
		state = stt;
	}

	public static void setSnake(ArrayList<Tuple> ssnake) {
		snake = ssnake;
	} 

	public static void setHeadSnake(Tuple hheadSnake) {
		headSnake = hheadSnake;
	}

	public static void setSizeSnake(int size) {
		sizeSnake = size;
	}

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

	//--------------------------------------------------------------
	// Fake Move
	//--------------------------------------------------------------
	public static void fakeMove(int dir) {
		predictState = copyArray(state);
		predictHeadSnake = new Tuple(headSnake.getX(),headSnake.getY());
		predictSnake = new ArrayList<Tuple>(snake);

		moveInterne(dir);
		moveExterne();
		deleteTail();

		// printArray(predictState);
	}

	//Moves the head of the snake and refreshes the positions in the arraylist
	 //1:right 2:left 3:top 4:bottom 0:nothing
	private static void moveInterne(int dir){
		 switch(dir){
		 	case 4:
				 predictHeadSnake.ChangeData(predictHeadSnake.x,(predictHeadSnake.y+1)%20);
				 predictSnake.add(new Tuple(predictHeadSnake.x,predictHeadSnake.y));
		 		break;
		 	case 3:
		 		if(predictHeadSnake.y-1<0){
		 			 predictHeadSnake.ChangeData(predictHeadSnake.x,19);
		 		 }
		 		else{
				 predictHeadSnake.ChangeData(predictHeadSnake.x,Math.abs(predictHeadSnake.y-1)%20);
		 		}
				 predictSnake.add(new Tuple(predictHeadSnake.x,predictHeadSnake.y));
		 		break;
		 	case 2:
		 		 if(predictHeadSnake.x-1<0){
		 			 predictHeadSnake.ChangeData(19,predictHeadSnake.y);
		 		 }
		 		 else{
		 			 predictHeadSnake.ChangeData(Math.abs(predictHeadSnake.x-1)%20,predictHeadSnake.y);
		 		 } 
		 		predictSnake.add(new Tuple(predictHeadSnake.x,predictHeadSnake.y));

		 		break;
		 	case 1:
				 predictHeadSnake.ChangeData(Math.abs(predictHeadSnake.x+1)%20,predictHeadSnake.y);
				 predictSnake.add(new Tuple(predictHeadSnake.x,predictHeadSnake.y));
		 		 break;
		 }
		 // System.out.printf("%d %d\n",predictHeadSnake.getX(),predictHeadSnake.getY());
	 }

	//Refresh the squares that needs to be 
	 private static void moveExterne(){
		 for(int i=0; i < predictSnake.size(); i++) {
		 	 predictState[predictSnake.get(i).getY()][predictSnake.get(i).getX()] = 2;
		 }
	 }


	 //Refreshes the tail of the snake, by removing the superfluous data in positions arraylist
	 //and refreshing the display of the things that is removed
	 private static void deleteTail(){
		 int cmpt = sizeSnake;
		 for(int i = predictSnake.size()-1;i>=0;i--){
			 if(cmpt==0){
				 Tuple t = predictSnake.get(i);
				 predictState[t.y][t.x] = 0;
			 }
			 else{
				 cmpt--;
			 }
		 }
		 cmpt = sizeSnake;
		 for(int i = predictSnake.size()-1;i>=0;i--){
			 if(cmpt==0){
			 	// System.out.printf("%d %d\n",predictSnake.get(i).getX() , predictSnake.get(i).getY());
				 predictSnake.remove(i);
			 }
			 else{
				 cmpt--;
			 }
		 }
	 }
 	
 	//--------------------------------------------------------------
 	// BFS ---- it will return shortest path
 	//--------------------------------------------------------------
	static int bfs(int[][] state,int hx,int hy,int dir,int dest) {
		Queue<Tuple> qu = new LinkedList<Tuple>();
		int[][] ca = copyArray(state);
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
	static int bfsProposal(int hx,int hy,int oldDir) {
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
			return dir;
		}
	    return 0;
	}

	static int bfdProposalAndPostprocessing(int hx,int hy,int oldDir) {
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
			fakeMove(dir);
			if ( bfs(predictState,hx,hy,dir,3) != -1 ) 
				return dir;
		}
	    return oldDir;
	}

	public static int proposeDirection(int hx,int hy,int oldDir) {
		return bfdProposalAndPostprocessing(hx,hy,oldDir);
	}

}
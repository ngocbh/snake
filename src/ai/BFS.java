package com.ai;

import java.util.ArrayList;
import java.util.LinkedList; 
import java.util.Queue; 
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import com.gui.*;

public class BFS {
	// //--------------------------------------------------------------
	// // Fake Move
	// //--------------------------------------------------------------
	// public static void fakeMove(int dir) {
	// 	AutoPlay.predictState = Utils.copyArray(AutoPlay.state);
	// 	AutoPlay.predictHeadSnake = new Tuple(AutoPlay.headSnake.getX(),AutoPlay.headSnake.getY());
	// 	AutoPlay.predictSnake = new ArrayList<Tuple>(AutoPlay.snake);

	// 	moveInterne(dir);
	// 	moveExterne();
	// 	deleteTail();

	// 	// printArray(AutoPlay.predictAutoPlay.state);
	// }

	// //Moves the head of the snake and refreshes the positions in the arraylist
	//  //1:right 2:left 3:top 4:bottom 0:nothing
	// private static void moveInterne(int dir){
	// 	 switch(dir){
	// 	 	case 4:
	// 			 AutoPlay.predictHeadSnake.ChangeData(AutoPlay.predictHeadSnake.x,(AutoPlay.predictHeadSnake.y+1)%20);
	// 			 AutoPlay.predictSnake.add(new Tuple(AutoPlay.predictHeadSnake.x,AutoPlay.predictHeadSnake.y));
	// 	 		break;
	// 	 	case 3:
	// 	 		if(AutoPlay.predictHeadSnake.y-1<0){
	// 	 			 AutoPlay.predictHeadSnake.ChangeData(AutoPlay.predictHeadSnake.x,19);
	// 	 		 }
	// 	 		else{
	// 			 AutoPlay.predictHeadSnake.ChangeData(AutoPlay.predictHeadSnake.x,Math.abs(AutoPlay.predictHeadSnake.y-1)%20);
	// 	 		}
	// 			 AutoPlay.predictSnake.add(new Tuple(AutoPlay.predictHeadSnake.x,AutoPlay.predictHeadSnake.y));
	// 	 		break;
	// 	 	case 2:
	// 	 		 if(AutoPlay.predictHeadSnake.x-1<0){
	// 	 			 AutoPlay.predictHeadSnake.ChangeData(19,AutoPlay.predictHeadSnake.y);
	// 	 		 }
	// 	 		 else{
	// 	 			 AutoPlay.predictHeadSnake.ChangeData(Math.abs(AutoPlay.predictHeadSnake.x-1)%20,AutoPlay.predictHeadSnake.y);
	// 	 		 } 
	// 	 		AutoPlay.predictSnake.add(new Tuple(AutoPlay.predictHeadSnake.x,AutoPlay.predictHeadSnake.y));

	// 	 		break;
	// 	 	case 1:
	// 			 AutoPlay.predictHeadSnake.ChangeData(Math.abs(AutoPlay.predictHeadSnake.x+1)%20,AutoPlay.predictHeadSnake.y);
	// 			 AutoPlay.predictSnake.add(new Tuple(AutoPlay.predictHeadSnake.x,AutoPlay.predictHeadSnake.y));
	// 	 		 break;
	// 	 }
	// 	 // System.out.printf("%d %d\n",AutoPlay.predictHeadSnake.getX(),AutoPlay.predictHeadSnake.getY());
	//  }

	// //Refresh the squares that needs to be 
	//  private static void moveExterne(){
	// 	 for(int i=0; i < AutoPlay.predictSnake.size(); i++) {
	// 	 	 AutoPlay.predictState[AutoPlay.predictSnake.get(i).getY()][AutoPlay.predictSnake.get(i).getX()] = 2;
	// 	 }
	//  }


	//  //Refreshes the tail of the snake, by removing the superfluous data in positions arraylist
	//  //and refreshing the display of the things that is removed
	//  private static void deleteTail(){
	// 	 int cmpt = AutoPlay.sizeSnake;
	// 	 for(int i = AutoPlay.predictSnake.size()-1;i>=0;i--){
	// 		 if(cmpt==0){
	// 			 Tuple t = AutoPlay.predictSnake.get(i);
	// 			 AutoPlay.predictState[t.y][t.x] = 0;
	// 		 }
	// 		 else{
	// 			 cmpt--;
	// 		 }
	// 	 }
	// 	 cmpt = AutoPlay.sizeSnake;
	// 	 for(int i = AutoPlay.predictSnake.size()-1;i>=0;i--){
	// 		 if(cmpt==0){
	// 		 	// System.out.printf("%d %d\n",AutoPlay.predictSnake.get(i).getX() , AutoPlay.predictSnake.get(i).getY());
	// 			 AutoPlay.predictSnake.remove(i);
	// 		 }
	// 		 else{
	// 			 cmpt--;
	// 		 }
	// 	 }
	//  }
 	
 // 	//--------------------------------------------------------------
 // 	// BFS ---- it will return shortest path
 // 	//--------------------------------------------------------------
	// static int bfs(int[][] sstate,int hx,int hy,int dir,int dest) {
	// 	Queue<Tuple> qu = new LinkedList<Tuple>();
	// 	int[][] ca = Utils.copyArray(sstate);
	// 	// if ( dest == 3 ) {
	// 	// 	System.out.println("==========in bfs==========");
	// 	// 	printArray(sAutoPlay.state);
	// 	// 	System.out.println("");
	// 	// }
	// 	int noBo = 0;
	// 	int ux = hx, uy = hy;
	// 	if ( dest == 1 ) {
	// 		if ( dir == 3 ) qu.add(new Tuple(ux-1,uy,0));
	// 		if ( dir == 4 ) qu.add(new Tuple(ux+1,uy,0));
	// 		if ( dir == 2 ) qu.add(new Tuple(ux,uy-1,0));
	// 		if ( dir == 1 ) qu.add(new Tuple(ux,uy+1,0));
	// 	} else {
	// 		if ( dir != 4 ) qu.add(new Tuple(ux-1,uy,0));
	// 		if ( dir != 3 ) qu.add(new Tuple(ux+1,uy,0));
	// 		if ( dir != 1 ) qu.add(new Tuple(ux,uy-1,0));
	// 		if ( dir != 2 ) qu.add(new Tuple(ux,uy+1,0));
	// 	}
	// 	while (qu.size() > 0) {
	// 		Tuple u = qu.poll();
	// 		ux = u.getX();
	// 		uy = u.getY();
	// 		int length = u.dir;
	// 		// System.out.printf("%d %d %d %d\n",ux,uy,length,ca[ux][uy]);

	// 		if ( sstate[ux][uy] == dest ) {
	// 			//return proposal;
	// 			if ( dest == 3 ) {
	// 				if ( ca[ux][uy] < 4 ) 
	// 					++noBo;
	// 			} 
	// 			if ( dest == 1 ) 
	// 				return length;
	// 		}

	// 		if ( ca[ux][uy] > 1 ) continue;
	// 		ca[ux][uy] = dir+4;
	// 		qu.add(new Tuple(ux-1,uy,length+1));
	// 		qu.add(new Tuple(ux+1,uy,length+1));
	// 		qu.add(new Tuple(ux,uy-1,length+1));
	// 		qu.add(new Tuple(ux,uy+1,length+1));
	// 	}
	// 	return -1;
	// }

	// //--------------------------------------------------------------
	// // BFS proposal --- 
	// //--------------------------------------------------------------
	// static int bfsProposal(int hx,int hy,int oldDir) {
	// 	ArrayList<Tuple> res = new ArrayList<Tuple>();

	// 	for (int i = 1; i <= 4; i++) { 
	// 		if ( (oldDir == 4 && i == 3 ) || ( oldDir == 3 && i == 4 ) ) continue;
	// 		if ( (oldDir == 1 && i == 2 ) || ( oldDir == 2 && i == 1 ) ) continue;
	// 		int length = bfs(AutoPlay.state,hx,hy,i,1);
	// 		if ( length != -1 ) 
	// 			res.add(new Tuple(i,length));
	// 		System.out.printf("%d %d\n",i,length);
	// 	}

	// 	Collections.sort(res, new Comparator<Tuple>() {
	//         @Override
	//         public int compare(Tuple u, Tuple v)
	//         {
	//         	return u.y - v.y;
	//         }
	//     });

	// 	for (int i = 0; i < res.size(); i++) {
	// 		// System.out.printf("%d %d\n",res.get(i).getX(),res.get(i).getY());
	// 		int dir = res.get(i).getX();
	// 		int length = res.get(i).getY();
	// 		return dir;
	// 	}

	// 	if ( AutoPlay.state[hx-1][hy] < 2 ) return 3;
	// 	if ( AutoPlay.state[hx+1][hy] < 2 ) return 4;
	// 	if ( AutoPlay.state[hx][hy-1] < 2 ) return 2;
	// 	if ( AutoPlay.state[hx][hy+1] < 2 ) return 1;
	//     return oldDir;
	// }

	// static int bfsProposalAndPostprocessing(int hx,int hy,int oldDir) {
	// 	ArrayList<Tuple> res = new ArrayList<Tuple>();

	// 	for (int i = 1; i <= 4; i++) { 
	// 		if ( (oldDir == 4 && i == 3 ) || ( oldDir == 3 && i == 4 ) ) continue;
	// 		if ( (oldDir == 1 && i == 2 ) || ( oldDir == 2 && i == 1 ) ) continue;
	// 		int length = bfs(AutoPlay.state,hx,hy,i,1);
	// 		if ( length != -1 ) 
	// 			res.add(new Tuple(i,length));
	// 		// System.out.printf("%d %d\n",i,length);
	// 	}

	// 	Collections.sort(res, new Comparator<Tuple>() {
	//         @Override
	//         public int compare(Tuple u, Tuple v)
	//         {
	//         	return u.y - v.y;
	//         }
	//     });


	// 	ArrayList<Tuple> dm = new ArrayList<Tuple>();
	// 	// System.out.println("==========haha==========");
	// 	for (int i = 0; i < res.size(); i++) {
	// 		// System.out.printf("%d %d\n",res.get(i).getX(),res.get(i).getY());
	// 		int dir = res.get(i).getX();
	// 		int length = res.get(i).getY();
	// 		fakeMove(dir);

	// 		// System.out.println("==========AutoPlay.state==========");
	// 		// printArray(AutoPlay.state);
	// 		// System.out.println("");
	// 		// System.out.println("==========AutoPlay.predictAutoPlay.state==========");
	// 		// printArray(AutoPlay.predictAutoPlay.state);
	// 		// System.out.println("");

	// 		int noBo = bfs(AutoPlay.predictState,AutoPlay.predictHeadSnake.getY(),AutoPlay.predictHeadSnake.getX(),dir,3);

	// 		// System.out.printf("%d %d\n",noBo,dir);
	// 		if ( noBo >= AutoPlay.predictState.length*2 + AutoPlay.predictState[0].length*2 -8 ) 
	// 			return dir;
	// 		else {
	// 			dm.add(new Tuple(dir,noBo));
	// 		}
	// 	}

	// 	Collections.sort(dm, new Comparator<Tuple>() {
	//         @Override
	//         public int compare(Tuple u, Tuple v)
	//         {
	//         	return u.y - v.y;
	//         }
	//     });

	//     for (int i = 0; i < dm.size(); i++) return dm.get(0).getX();

	// 	for (int i = 0; i < res.size(); i++) {
	// 		// System.out.printf("%d %d\n",res.get(i).getX(),res.get(i).getY());
	// 		int dir = res.get(i).getX();
	// 		int length = res.get(i).getY();
	// 		return dir;
	// 	}

	// 	//Xu huong di ve duoi
	// 	Tuple tail = AutoPlay.snake.get(0);
	// 	int xp = 0, yp = 0;
	// 	if ( tail.x > hx ) xp = 1; 
	// 	else if ( tail.x < hx ) xp = -1;
	// 	if ( tail.y > hy ) yp = 1; 
	// 	else if ( tail.y < hy ) yp = -1;

	// 	if ( AutoPlay.state[hx-1][hy] < 2 && xp == -1 ) return 3;
	// 	if ( AutoPlay.state[hx+1][hy] < 2 && xp == 1) return 4;
	// 	if ( AutoPlay.state[hx][hy-1] < 2 && yp == -1) return 2;
	// 	if ( AutoPlay.state[hx][hy+1] < 2 && yp == 1) return 1;

	// 	//chon dai
	//     if ( AutoPlay.state[hx-1][hy] < 2 ) return 3;
	// 	if ( AutoPlay.state[hx+1][hy] < 2 ) return 4;
	// 	if ( AutoPlay.state[hx][hy-1] < 2 ) return 2;
	// 	if ( AutoPlay.state[hx][hy+1] < 2 ) return 1;

	// 	//tu sat di :)
	//     return oldDir;
	// }
}
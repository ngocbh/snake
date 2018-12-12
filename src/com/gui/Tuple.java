package com.gui;

import java.util.Comparator;

public class Tuple { 
	public static final int inf = 1000000000 + 7;
	public  int x; 
	public  int y; 
	public int xf;
	public int yf;
	public int length;
	public int fscore;
	public int dir = 0;

	public static Comparator<Tuple> tupleComparator() {
		return new Comparator<Tuple>() {
			@Override
			public int compare(Tuple u,Tuple v) {
				if ( u.fscore != v.fscore ) 
					return u.fscore - v.fscore;
				else 
					return v.length - u.length;
			}
		};
	}

	public Tuple() {
		this.x = 0;
		this.y = 0;
		this.length = 0;
		this.dir = 0;
		this.fscore = inf;
	}

	public Tuple(int x, int y) { 
	    this.x = x; 
	    this.y = y; 
	    this.length = 0;
	    this.dir = 0;
	    this.fscore = inf;
	} 

	public Tuple(int x,int y,int dir) {
	  	this.x = x;
	  	this.y = y;
	  	this.length = 0;
	  	this.dir = dir;
	  	this.fscore = inf;
	}

	public Tuple(int x,int y,int dir,int length,int fscore) {
	  	this.x = x;
	  	this.y = y;
	  	this.dir = dir;
	  	this.length = length;
	  	this.fscore = fscore;
	}

	public Tuple(int x,int y,int dir,int length,Tuple dest) {
	  	this.x = x;
	  	this.y = y;
	  	this.dir = dir;
	  	this.length = length;
	  	this.fscore = length + Math.abs(dest.x - x) + Math.abs(dest.y - y);
	}

	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getLength(){
		return length;
	}
	public int getXf(){
	 	return xf;
	}
	public int getYf(){
	  	return yf;
	}
	public int getDir(){
		return dir;
	}

	public Tuple next(int dir) {
		if (dir == 1) 
			return new Tuple(x,y+1,dir);
		else if (dir == 2) 
			return new Tuple(x,y-1,dir);
		else if (dir == 3)
			return new Tuple(x-1,y,dir);
		return new Tuple(x+1,y,dir);
	}

	public Tuple prev(int dir) {
		if (dir == 1)
			return new Tuple(x,y-1,dir);
		else if (dir == 2)
			return new Tuple(x,y+1,dir);
		else if (dir == 3) 
			return new Tuple(x+1,y,dir);
		return new Tuple(x-1,y,dir);
	}

	public Tuple left() {
		if ( dir == 1 ) 
			return this.next(3);
		else if ( dir == 2 ) 
			return this.next(4);
		else if ( dir == 3 ) 
			return this.next(2);
		return this.next(1);
	}

	public Tuple right() {
		if ( dir == 1 ) 
			return this.next(4);
		else if ( dir == 2 ) 
			return this.next(3);
		else if ( dir == 3 ) 
			return this.next(1);
		return this.next(2);
	}

	public static int opposite(int dir) {
		if ( dir == 1 ) 
			return 2;
		else if ( dir == 2 ) 
			return 1;
		else if ( dir == 3 ) 
			return 4;
		return 3;
	}
	  
	public void print() {
	  	System.out.printf("%d %d %d %d %d\n",x,y,dir,length,fscore);
	}
} 
package com.gui;

public class Tuple { 
	  public  int x; 
	  public  int y; 
	  public int xf;
	  public int yf;
	  public int length;
	  public int dir = 0;

	  public Tuple(int x, int y) { 
	    this.x = x; 
	    this.y = y; 
	    this.length = 0;
	    this.dir = 0;
	  } 

	  public Tuple(int x,int y,int dir) {
	  	this.x = x;
	  	this.y = y;
	  	this.length = 0;
	  	this.dir = dir;
	  }

	  public Tuple(int x,int y,int length,int dir) {
	  	this.x = x;
	  	this.y = y;
	  	this.length = length;
	  	this.dir = dir;
	  }

	  public void ChangeData(int x, int y){
		    this.x = x; 
		    this.y = y; 
	  }

	  public void ChangeData(int x,int y,int dir) {
	  	this.x = x;
	  	this.y = y;
	  	this.dir = dir;
	  }

	  public void ChangeData(int x,int y,int dir,int length) {
	  	this.x = x;
	  	this.y = y;
	  	this.length = length;
	  	this.dir = dir;
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
	  	System.out.printf("%d %d %d %d\n",x,y,dir,length);
	}
} 
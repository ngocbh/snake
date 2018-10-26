package com.gui;

public class Tuple { 
	  public  int x; 
	  public  int y; 
	  public int xf;
	  public int yf;
	  public int rem;
	  public int length = 0;
	  public Tuple(int x, int y) { 
	    this.x = x; 
	    this.y = y; 
	    this.rem = 0;
	    this.length = 0;
	  } 

	  public Tuple(int x,int y,int length) {
	  	this.x = x;
	  	this.y = y;
	  	this.rem = 0;
	  	this.length = length;
	  }

	  public Tuple(int x,int y,int rem,int length) {
	  	this.x = x;
	  	this.y = y;
	  	this.rem = rem;
	  	this.length = length;
	  }

	  public void ChangeData(int x, int y){
		    this.x = x; 
		    this.y = y; 
	  }

	  public void ChangeData(int x,int y,int length) {
	  	this.x = x;
	  	this.y = y;
	  	this.length = length;
	  }

	  public void ChangeData(int x,int y,int rem,int length) {
	  	this.x = x;
	  	this.y = y;
	  	this.rem = rem;
	  	this.length = length;
	  }

	  public int getX(){
		  return x;
	  }
	  public int getY(){
		  return y;
	  }
	  public int getRem(){
	  	return rem;
	  }
	  public int getXf(){
		  return xf;
	  }
	  public int getYf(){
		  return yf;
	  }
	  public int getLength(){
	  	return length;
	  }

		  
		  
} 
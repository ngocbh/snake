package com.test;

import java.util.*;
import java.io.*;
import java.io.FileNotFoundException;

import com.gui.*;
import com.ai.*;

class Test {
	public static void main(String[] args) {
		File file = new File(args[0]); 

  		BufferedReader br;
  		try
	    {
			br = new BufferedReader(new FileReader(file));
			int[][] state = new int[20][20];

			String st; 
			try {
				int i = 0;
			 	while ((st = br.readLine()) != null) {
			 		for (int j = 0; j < st.length(); j++) 
			 			state[i][j] = (int) (st.charAt(j) - '0');
			 		++i;
			 	}

			 	Utils.printArray(state);
			 	LinkedList<Tuple> shortestPath = Utils.findShortestPath_a_star(state,17,3,17,13);
			 	for (int x = 0; x < shortestPath.size(); x++)
			 		shortestPath.get(x).print();
			}
			catch (IOException ex) {
				return;
			}
	    }
	    catch (FileNotFoundException ex)  
	    {
	        // insert code to run when exception occurs
	        return;
	    }
	  	 
	}
}
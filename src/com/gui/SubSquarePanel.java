package com.gui;

import java.awt.Color;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SubSquarePanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
        Random rd = new Random();
        public int x = rd.nextInt(9);
        
        
	ArrayList<Color> C = new ArrayList<Color>(Arrays.asList(Color.white, Color.red, Color.darkGray,Color.orange,Color.white));
	public static int color = 0;

	public SubSquarePanel(int d){
		color = d;
                switch(x) {
                    case 1: C = new ArrayList<Color>(Arrays.asList(Color.white, Color.red, Color.darkGray,Color.blue,Color.white)); break;
                    case 2: C = new ArrayList<Color>(Arrays.asList(Color.white, Color.red, Color.darkGray,Color.green,Color.white)); break;
                    case 3: C = new ArrayList<Color>(Arrays.asList(Color.white, Color.red, Color.darkGray,Color.yellow,Color.white)); break;
                    case 4: C = new ArrayList<Color>(Arrays.asList(Color.white, Color.red, Color.darkGray,Color.orange,Color.white)); break;
                    case 5: C = new ArrayList<Color>(Arrays.asList(Color.white, Color.red, Color.darkGray,Color.pink,Color.white)); break;
                    case 6: C = new ArrayList<Color>(Arrays.asList(Color.white, Color.red, Color.darkGray,Color.gray,Color.white)); break;
                    case 7: C = new ArrayList<Color>(Arrays.asList(Color.white, Color.red, Color.darkGray,Color.red,Color.white)); break;
                    case 8: C = new ArrayList<Color>(Arrays.asList(Color.white, Color.red, Color.darkGray,Color.lightGray,Color.white)); break;
                    
                }
		this.setBackground(C.get(color));
	}
	
	public void ChangeColor(int d){
		color = d;
                switch(x) {
                    case 1: C = new ArrayList<Color>(Arrays.asList(Color.white, Color.red, Color.darkGray,Color.blue,Color.white)); break;
                    case 2: C = new ArrayList<Color>(Arrays.asList(Color.white, Color.red, Color.darkGray,Color.green,Color.white)); break;
                    case 3: C = new ArrayList<Color>(Arrays.asList(Color.white, Color.red, Color.darkGray,Color.yellow,Color.white)); break;
                    case 4: C = new ArrayList<Color>(Arrays.asList(Color.white, Color.red, Color.darkGray,Color.orange,Color.white)); break;
                    case 5: C = new ArrayList<Color>(Arrays.asList(Color.white, Color.red, Color.darkGray,Color.pink,Color.white)); break;
                    case 6: C = new ArrayList<Color>(Arrays.asList(Color.white, Color.red, Color.darkGray,Color.gray,Color.white)); break;
                    case 7: C = new ArrayList<Color>(Arrays.asList(Color.white, Color.red, Color.darkGray,Color.red,Color.white)); break;
                    case 8: C = new ArrayList<Color>(Arrays.asList(Color.white, Color.red, Color.darkGray,Color.lightGray,Color.white)); break;
                }                
		this.setBackground(C.get(color));
		// this.repaint();
	}
	
}


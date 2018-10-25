import java.util.ArrayList;
import java.util.Arrays;
import java.awt.Color;
import javax.swing.JPanel;
import java.awt.GridLayout;
import javax.swing.JFrame;

public class DataOfSquare extends JPanel {

	
	//ArrayList that'll contain the colors
	public int[] cross = {0,0,0,0,0};
	public ArrayList<ArrayList<SubSquarePanel>> GridChild;
	private int height = 5;
	private int width = 5;
	public int obj = 0;

	//obj : 0: space ; 1: food; 2: snake; 3: border
	public DataOfSquare(int object){
		obj = object;

		GridChild = new ArrayList<ArrayList<SubSquarePanel>>();
		ArrayList<SubSquarePanel> data;

		// Creates Threads and its data and adds it to the arrayList
		for(int i=0;i<width;i++){
			data= new ArrayList<SubSquarePanel>();
			for(int j=0;j<height;j++){
				SubSquarePanel c = new SubSquarePanel(obj);
				data.add(c);
			}
			GridChild.add(data);
		}

		// Setting up the layout of the panel
		setLayout(new GridLayout(this.width,this.height,0,0));
		setBackground(Color.white);
		// Start & pauses all threads, then adds every square of each thread to the panel
		for(int i=0;i<width;i++){
			for(int j=0;j<height;j++){
				add(GridChild.get(i).get(j));
			}
		}

	}

	public void lightMeUp(int pos,int value,boolean isHead){
		this.obj = value;
		switch (pos) {
			case 1:
				for (int i = 1; i < width-1; i++) 
					this.GridChild.get(0).get(i).ChangeColor(value);
				break;
			case 2:
				for (int i = 1; i < width-1; i++) 
					this.GridChild.get(height-1).get(i).ChangeColor(value);
				break;
			case 3:
				for (int i = 1; i < height-1; i++) 
					this.GridChild.get(i).get(0).ChangeColor(value);
				break;
			case 4:
				for (int i = 1; i < height-1; i++) 
					this.GridChild.get(i).get(width-1).ChangeColor(value);
				break;
			case 5:
				for (int i = 1; i < width-1; i++) 
					for (int j = 1; j < height-1; j++)
						this.GridChild.get(i).get(j).ChangeColor(value);
				if ( isHead )
					this.GridChild.get(2).get(2).ChangeColor(4);
				break;
		}

		if ( value != 2 ) {
			for (int i = 1; i < width-1; i++) {
				this.GridChild.get(0).get(i).ChangeColor(0); 
				this.GridChild.get(height-1).get(i).ChangeColor(0);
			}
			for (int i = 1; i < height-1; i++) {
				this.GridChild.get(i).get(0).ChangeColor(0);
				this.GridChild.get(i).get(width-1).ChangeColor(0);
			}
		}
		this.repaint();
	}
}

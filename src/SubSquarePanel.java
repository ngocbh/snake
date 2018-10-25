import java.awt.Color;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.Arrays;

public class SubSquarePanel extends JPanel{
	
	private static final long serialVersionUID = 1L;	
	ArrayList<Color> C =new ArrayList<Color>(Arrays.asList(Color.white, Color.BLUE, Color.darkGray));
	public static int color = 0;

	public SubSquarePanel(int d){
		color = d;
		this.setBackground(C.get(color));
	}
	
	public void ChangeColor(int d){
		color = d;
		this.setBackground(C.get(color));
		this.repaint();
	}
	
}


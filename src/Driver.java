import javax.swing.JFrame;

public class Driver {

	public static void main (String[] args) {
		PongWindow window = new PongWindow ();
		window.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		window.setVisible (true);
	}

}

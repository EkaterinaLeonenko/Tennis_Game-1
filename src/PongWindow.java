
import javax.swing.JFrame;
import javax.swing.Timer;

public class PongWindow extends JFrame {
	public PongWindow () {
		super ();
		
		setTitle ("TennisGame");
		setSize (640, 480);
		
		TennisGame content = new TennisGame (Player.KEYBOARD, Player.CPU_HARD);
		content.acceleration = true;
		getContentPane ().add (content);
		
		addMouseListener (content);
		addKeyListener (content);
		
		Timer timer = new Timer (20, content);
		timer.start ();
	}
}


import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class TennisGame extends JPanel implements ActionListener, MouseListener, KeyListener {
	// Property of the ball
	private static final int RADIUS = 10; // Radius
	private static final int START_SPEED = 9; // Initial speed
	private static final int ACCELERATION = 125; //speed +1

	//Property of the Platform
	private static final int SPEED = 4; // Platform Speed
	private static final int HEIGHT = 50; // height of the platform
	private static final int WIDTH = 20;
	private static final int TOLERANCE = 40;
	private static final int PADDING = 10;
	
	private Player player1;
	private Player player2;
	
	private boolean new_game = true;
	
	private int ball_x;
	private int ball_y;
	private double ball_x_speed;
	private double ball_y_speed;
	
	public boolean acceleration = false;
	private int ball_acceleration_count;
	
	private boolean mouse_inside = false;
	private boolean key_up = false;
	private boolean key_down = false;
	
	// Constructor
	public TennisGame (int p1_type, int p2_type) {
		super ();
		setBackground (new Color (0, 0, 0));
		
		player1 = new Player (p1_type);
		player2 = new Player (p2_type);
	}
	
	// Compute destination of the ball
	private void computeDestination (Player player) {
		if (ball_x_speed > 0)
			player.destination = ball_y + (getWidth() - PADDING - WIDTH - RADIUS - ball_x) * (int)(ball_y_speed) / (int)(ball_x_speed);
		else
			player.destination = ball_y - (ball_x - PADDING - WIDTH - RADIUS) * (int)(ball_y_speed) / (int)(ball_x_speed);
		
		if (player.destination <= RADIUS)
			player.destination = 2 * PADDING - player.destination;
		
		if (player.destination > getHeight() - 10) {
			player.destination -= RADIUS;
			if ((player.destination / (getHeight() - 2 * RADIUS)) % 2 == 0)
				player.destination = player.destination % (getHeight () - 2 * RADIUS);
			else
				player.destination = getHeight() - 2 * RADIUS - player.destination % (getHeight () - 2 * RADIUS);
			player.destination += RADIUS;
		}
	}
	
	// Set new position of the player
	private void movePlayer (Player player, int destination) {
		int distance = Math.abs (player.position - destination);
		
		if (distance != 0) {
			int direction = - (player.position - destination) / distance;
			
			if (distance > SPEED)
				distance = SPEED;
			
			player.position += direction * distance;
			
			if (player.position - HEIGHT < 0)
				player.position = HEIGHT;
			if (player.position + HEIGHT > getHeight())
				player.position = getHeight() - HEIGHT;
		}
	}
	
	// Compute player position
	private void computePosition (Player player) {
		// MOUSE
		if (player.getType() == Player.MOUSE) {
			if (mouse_inside) {
				int cursor = getMousePosition().y;
				movePlayer (player, cursor);
			}
		}
		// KEYBOARD
		else if (player.getType() == Player.KEYBOARD) {
			if (key_up && !key_down) {
				movePlayer (player, player.position - SPEED);
			}
			else if (key_down && !key_up) {
				movePlayer (player, player.position + SPEED);
			}
		}
		// CPU HARD
		else if (player.getType() == Player.CPU_HARD) {
			movePlayer (player, player.destination);
		}
		// CPU EASY
		else if (player.getType() == Player.CPU_EASY) {
			movePlayer (player, ball_y);
		}
	}
	
	// Draw
	public void paintComponent (Graphics g) {
		super.paintComponent (g);
		
		// Preparing the playing field
		if (new_game) {
			ball_x = getWidth () / 2;
			ball_y = getHeight () / 2;
			
			double phase = Math.random () * Math.PI / 2 - Math.PI / 4;
			ball_x_speed = (int)(Math.cos (phase) * START_SPEED);
			ball_y_speed = (int)(Math.sin (phase) * START_SPEED);
			
			ball_acceleration_count = 0;
			
			if (player1.getType() == Player.CPU_HARD || player1.getType() == Player.CPU_EASY) {
				player1.position = getHeight () / 2;
				computeDestination (player1);
			}
			if (player2.getType() == Player.CPU_HARD || player2.getType() == Player.CPU_EASY) {
				player2.position = getHeight () / 2;
				computeDestination (player2);
			}
			
			new_game = false;
		}
		
		// Calculate the position of the first player
		if (player1.getType() == Player.MOUSE || player1.getType() == Player.KEYBOARD || ball_x_speed < 0)
			computePosition (player1);
		
		// Calculate the position of the second player
		if (player2.getType() == Player.MOUSE || player2.getType() == Player.KEYBOARD || ball_x_speed > 0)
			computePosition (player2);
		
		// Calculates the position of the ball
		if(player1.points<40 && player2.points<40) {ball_x += ball_x_speed;
		ball_y += ball_y_speed;
		if (ball_y_speed < 0) // Hack to fix double-to-int conversion
			ball_y ++;}
		
		// Accelerate the ball
		if (acceleration) {
			ball_acceleration_count ++;
			if (ball_acceleration_count == ACCELERATION) {
				ball_x_speed = ball_x_speed + (int)ball_x_speed / Math.hypot ((int)ball_x_speed, (int)ball_y_speed) * 2;
				ball_y_speed = ball_y_speed + (int)ball_y_speed / Math.hypot ((int)ball_x_speed, (int)ball_y_speed) * 2;
				ball_acceleration_count = 0;
			}
		}
		
		// Border-collision LEFT
		if (ball_x <= PADDING + WIDTH + RADIUS) {
			int collision_point = ball_y + (int)(ball_y_speed / ball_x_speed * (PADDING + WIDTH + RADIUS - ball_x));
			if (collision_point > player1.position - HEIGHT - TOLERANCE && 
			    collision_point < player1.position + HEIGHT + TOLERANCE) {
				ball_x = 2 * (PADDING + WIDTH + RADIUS) - ball_x;
				ball_x_speed = Math.abs (ball_x_speed);
				ball_y_speed -= Math.sin ((double)(player1.position - ball_y) / HEIGHT * Math.PI / 4)
				                * Math.hypot (ball_x_speed, ball_y_speed);
				if (player2.getType() == Player.CPU_HARD)
					computeDestination (player2);
			}
			else {
				
				if(player2.points<30) {player2.points =player2.points+15;
				new_game = true;}
				else {
					player2.points =player2.points+10;
					new_game = true;
				}
			}
		}
		
		// Border-collision RIGHT
		if (ball_x >= getWidth() - PADDING - WIDTH - RADIUS) {
			int collision_point = ball_y - (int)(ball_y_speed / ball_x_speed * (ball_x - getWidth() + PADDING + WIDTH + RADIUS));
			if (collision_point > player2.position - HEIGHT - TOLERANCE && 
			    collision_point < player2.position + HEIGHT + TOLERANCE) {
				ball_x = 2 * (getWidth() - PADDING - WIDTH - RADIUS ) - ball_x;
				ball_x_speed = -1 * Math.abs (ball_x_speed);
				ball_y_speed -= Math.sin ((double)(player2.position - ball_y) / HEIGHT * Math.PI / 4)
				                * Math.hypot (ball_x_speed, ball_y_speed);
				if (player1.getType() == Player.CPU_HARD)
					computeDestination (player1);
			}
			else {
				if(player1.points<30) {player1.points =player1.points+15;
				new_game = true;}
				else {player1.points =player1.points+10;
				new_game = true;
					
				}
			}
		}
		
		// Border-collision TOP
		if (ball_y <= RADIUS) {
			ball_y_speed = Math.abs (ball_y_speed);
			ball_y = 2 * RADIUS - ball_y;
		}
		
		// Border-collision BOTTOM
		if (ball_y >= getHeight() - RADIUS) {
			ball_y_speed = -1 * Math.abs (ball_y_speed);
			ball_y = 2 * (getHeight() - RADIUS) - ball_y;
		}
		
		// Draw platform
		g.setColor (Color.WHITE);
		g.fillRect (PADDING, player1.position - HEIGHT, WIDTH, HEIGHT * 2);
		g.fillRect (getWidth() - PADDING - WIDTH, player2.position - HEIGHT, WIDTH, HEIGHT * 2);
		
		// Draw the ball
		if(player1.points<40 && player2.points<40) {
		g.fillOval (ball_x - RADIUS, ball_y - RADIUS, RADIUS*2, RADIUS*2);}
		
		// Draw points
		g.drawString (player1.points+" ", getWidth() / 2 - 20, 20);
		g.drawString (player2.points+" ", getWidth() / 2 + 20, 20);
		if (player1.points> player2.points)g.drawString ("advantage player1", getWidth() / 2-50, 50);
		if (player2.points> player1.points)g.drawString ("advantage player2", getWidth() / 2-50, 50);
		if(player1.points==40||player2.points==40)g.drawString ("game over", getWidth() / 2-25, 75);
	}
	
	// New frame
	public void actionPerformed (ActionEvent e) {
		repaint ();
	}
	
	// Mouse inside
	public void mouseEntered (MouseEvent e) {
		mouse_inside = true;
	}
	
	// Mouse outside
	public void mouseExited (MouseEvent e) {
		mouse_inside = false;
	}
	
	// Mouse pressed
	public void mousePressed (MouseEvent e) {}
	
	// Mouse released
	public void mouseReleased (MouseEvent e) {}
		
	// Mouse clicked
	public void mouseClicked (MouseEvent e) {}
	
	// Key pressed
	public void keyPressed (KeyEvent e) {
//		System.out.println ("Pressed "+e.getKeyCode()+"   "+KeyEvent.VK_UP+" "+KeyEvent.VK_DOWN);
		if (e.getKeyCode() == KeyEvent.VK_UP)
			key_up = true;
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			key_down = true;
	}
	
	// Key released
	public void keyReleased (KeyEvent e) {
//		System.out.println ("Released "+e.getKeyCode());
		if (e.getKeyCode() == KeyEvent.VK_UP)
			key_up = false;
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			key_down = false;
	}
	
	// Key released
	public void keyTyped (KeyEvent e) {}
	public String getScore() {
		System.out.println("I am method for displaying score");
		// Here is the format of the scores: "player1Score - player2Score"
		// "0 - 0"
		// "15 - 15"
		// "30 - 30"
		// "deuce"
		// "15 - 0", "0 - 15"
		// "30 - 0", "0 - 30"
		// "40 - 0", "0 - 40"
		// "30 - 15", "15 - 30"
		// "40 - 15", "15 - 40"
		// "advantage player1"
		// "advantage player2"
		// "game player1"
		// "game player2"

		// TO BE IMPLEMENTED
		return "";
	}
}

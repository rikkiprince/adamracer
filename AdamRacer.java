import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;

public class AdamRacer extends JFrame implements MouseListener, MouseMotionListener
{
	private BufferedImage track;

	public AdamRacer()
	{
		track = new BufferedImage(400,400, BufferedImage.TYPE_INT_ARGB);


		/* HOUSEKEEPING */
		this.addMouseMotionListener(this);
		this.setSize(400,400);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.repaint();
	}

	public static void main(String args[])
	{
		JFrame f = new AdamRacer();
	}


	/* MOUSELISTENER */
	public void mouseClicked(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}

	/* MOUSEMOTIONLISTENER */
	public void mouseDragged(MouseEvent e)
	{
		System.out.println(e.getX() + " " + e.getY());
		Graphics g = track.getGraphics();
		g.setColor(Color.red);
		g.fillOval(e.getX(), e.getY(), 20, 20);

		this.repaint();
	}

	public void mouseMoved(MouseEvent e)
	{
	}

	public void paint(Graphics g)
	{
		g.drawImage(track, 0, 0, Color.lightGray, null);	// don't want an ImageObserver, so set to null
	}
}

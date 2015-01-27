/**	TODO
*	Check if line intersects track
*	Need track.inside(Point p)
*	Need track.intersect(Line2D l)
*	Need Track class
 *	Start/finish line
 *	Networking
 *	Turns
 *	Is the player allowed to choose whether they crash into inside or outside wall?  If not, might make it hard as would have to work out whether corner was convex or concave...
 *	Auto-scroll!
 *	Animated car movement
 *	Car noises
 */


import com.kitfox.svg.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.regex.*;

public class SVGtoPolygon implements MouseListener, MouseMotionListener, AdjustmentListener
{
	private SVGUniverse universe;
	private SVGDiagram diagram;
	private URI uri;

	private JFrame window;
	private SVGDisplayPanel svgDisplayPanel;
	private MyScrollPane scroll;
	
	private int numPlayers;
	private int currentPlayer;
	private ArrayList<Player> players;
	private ColourQueue cq;

	
	public static void main(String[] args) throws Exception
	{
		SVGtoPolygon program = new SVGtoPolygon();
	}

	public SVGtoPolygon() throws Exception
	{
		System.out.println("SVG!");
		
		
		// SVG SET-UP
		universe = new SVGUniverse();

		String fileName = "Test2.svg";
		System.out.println("Loading and parsing "+fileName);
		uri = universe.loadSVG(new FileInputStream(fileName), "test");

		System.out.println(uri);

		SVGElement element = universe.getElement(uri);

		display(element);

		diagram = universe.getDiagram(uri);
		
		
		
		// PLAYER SET-UP
		String playersIn = JOptionPane.showInputDialog("Please type the number of players", "1");
		this.numPlayers = (new Integer(playersIn)).intValue();
		this.currentPlayer = 0;
		
		this.players = new ArrayList();
		
		this.cq = new ColourQueue();
		
		for(int i=0; i<numPlayers; i++)
		{
			String name = "Bob "+i;	//JOptionPane.showInputDialog("Please enter Player "+(i+1)+"'s name", randomNameGenerator());
			this.players.add(new Player(name, cq.get()));
		}
		
		
		// WINDOW SET-UP
		window = new JFrame("SVG");
		window.setSize(400, 400);
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		scroll = new MyScrollPane(this.players);

		svgDisplayPanel = new SVGDisplayPanel();
		//svgDisplayPanel.setScale(0.001f);
		svgDisplayPanel.setDiagram(diagram);

		//scroll.add(svgDisplayPanel);
		scroll.setViewportView(svgDisplayPanel);

		window.add(scroll);

		svgDisplayPanel.addMouseListener(this);
		svgDisplayPanel.addMouseMotionListener(this);
		scroll.getHorizontalScrollBar().addAdjustmentListener(this);
		scroll.getVerticalScrollBar().addAdjustmentListener(this);

		window.repaint();
	}
	
	private String randomNameGenerator()
	{
		String url = "http://www.behindthename.com/random/random.php?number=1&gender=both&surname=&all=yes";
		
		String page = "";
		String line = "";
		
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			while((line = br.readLine()) != null)
			{
				page += line;
			}
		}
		catch(Exception e)
		{
			return "Jeff";
		}
		
		//System.out.println(page);
		String regex = "<br><p>Your random name is:<br><center><p><font class=\"heavymedium\"> <a href=.* class=\"plain\">([A-Za-z]*)</a>";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(page);
		matcher.find();
		
		return matcher.group(1);
	}

	public void mouseClicked(MouseEvent e)
	{
		// add point to current player
		// move to next player
		// set previous point in scroll pane for next player
		try
		{
			//System.out.println(e.getX() + " " + e.getY());
			Vector v = diagram.pick(new Point(e.getX(), e.getY()), null);
			System.out.println(v.size());
			
			//this.players.get(this.currentPlayer).add(e.getX(), e.getY());
			scroll.placePoint(new Point(e.getX(), e.getY()));
			this.currentPlayer = (this.currentPlayer+1) % this.numPlayers;
			
			System.out.println("Player "+this.currentPlayer+"'s go!");
			scroll.setCurrentPlayer(this.currentPlayer);
			
			if(this.currentPlayer == 0)
			{
				// get prediction for next round
				for(int i = 0; i < this.numPlayers; i++)
				{
					int response = -1;
					Object[] options = {"Accelerate Hard (+2)", "Accelerate (+1)", "Maintain Speed (+/- 0)", "Ease Off (-1)", "Brake (-2)", "Brake Hard (-3)"};
					while(response < 0)
					{
						response = JOptionPane.showOptionDialog(null, "Player "+(i+1)+": Would you like to change your speed?", "Vroom-Vroom!!", 
							JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
							null, options, options[2]);
					}
					
					players.get(i).modifySpeed(2-response);
				}
			}
			
			window.repaint();
		}
		catch(Exception ex)
		{
			System.out.println(ex);
		}
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
	
	public void mouseMoved(MouseEvent e)
	{
		scroll.setCurrentPoint(e.getX(), e.getY());
		
		scroll.repaint();
	}
	
	public void mouseDragged(MouseEvent e)
	{
	}
	
	public void adjustmentValueChanged(AdjustmentEvent e)
	{
		scroll.repaint();
	}


	public static void display(SVGElement element)
	{
		display(element, 0);
	}

	public static void display(SVGElement element, int level)//, Vector paths)
	{
		for(int i=0; i<level; i++) System.out.print("  ");
		System.out.println(element.getClass());

		if(element instanceof com.kitfox.svg.Path)
		{
			Shape s = ((com.kitfox.svg.Path)element).getShape();
			System.out.println(s.getClass());
		}
		//paths.add(

		// extract and process child elements
		Vector<SVGElement> v = element.getChildren(null);

		for(SVGElement e : v)
		{
			display(e, level+1);
		}
	}

public class Position
{
	private Point position;
	private int speed;
	
	public Position(int x, int y, int s)
	{
		this(new Point(x, y), s);
	}
	
	public Position(Point p, int s)
	{
		this.position = p;
		this.speed = s;
	}
	
	public int getSpeed()
	{
		return this.speed;
	}
	
	public int getX()
	{
		return (int)this.position.getX();
	}
	
	public int getY()
	{
		return (int)this.position.getY();
	}
	
	public void paint(Graphics g, Point scrollOffset)
	{
		g.fillOval((int)(this.position.getX()-4-scrollOffset.getX()),(int)(this.position.getY()-4-scrollOffset.getY()), 8,8);
		g.drawOval((int)(this.position.getX()-8-scrollOffset.getX()),(int)(this.position.getY()-8-scrollOffset.getY()), 16,16);
	}
}

public class Player
{
	private ArrayList<Position> positions;
	private String name;
	private Color colour;
	
	private int currentSpeed;
	
	public Player(String name, Color colour)
	{
		positions = new ArrayList();
		this.name = name;
		this.colour = colour;
		
		this.currentSpeed = 0;
	}
	
	public void add(int x, int y)
	{
		add(new Point(x, y));
	}
	
	public void add(Point p)
	{
		add(new Position(p, currentSpeed));
	}
	
	public void add(Position pos)
	{
		positions.add(pos);
	}
	
	public Position getLastPosition()
	{
		if(this.positions.size() > 0)
			return this.positions.get(this.positions.size()-1);
		else
			return null;
	}
	
	public Color getColour()
	{
		return this.colour;
	}
	
	public int getSpeed()
	{
		return currentSpeed;
	}
	
	public void modifySpeed(int i)
	{
		if(i < -3 || i > 2)
		{
			System.out.println("Cannot slow down or speed up by that much!");
		}
		else
		{
			this.currentSpeed += i;
		}
	}
	
	public void paint(Graphics g, Point scrollOffset)
	{
		Color originalColour = g.getColor();
		g.setColor(this.getColour());
		
		for(Position p : this.positions)
		{
			p.paint(g, scrollOffset);
		}
		
		g.setColor(originalColour);
	}
}

public class MyScrollPane extends JScrollPane
{
	private Point current;
	private ArrayList<Player> players;
	private int currentPlayer;
	

	private double speed = 10;
	
	
	public MyScrollPane(ArrayList<Player> players)
	{
		this.players = players;
		
		int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
		double dpc = dpi/2.54;
		
		this.speed = (int)dpc;
	}
	
	public void setCurrentPoint(int x, int y)
	{
		this.setCurrentPoint(new Point(x, y));
	}
	
	public void setCurrentPoint(Point point)
	{
		Player player = this.players.get(this.currentPlayer);
		Position old = player.getLastPosition();
		
		if(old != null)
		{
			double A = point.getX()-old.getX();
			double O = point.getY()-old.getY();
			double H = Math.sqrt(A*A + O*O);
		
			double h = player.getSpeed() * this.speed;
			
			double a = A * (h/H);
			double o = O * (h/H);
			
			this.current = new Point((int)(old.getX() + a), (int)(old.getY() + o));
		}
		else
		{
			this.current = point;
		}
	}
	
	public void placePoint(Point point)
	{
		this.setCurrentPoint(point);
		
		Player player = this.players.get(this.currentPlayer);
		player.add(this.current);
	}
	
	public void setCurrentPlayer(int currentPlayer)
	{
		this.currentPlayer = currentPlayer;
	}
	
	public void paint(Graphics g)
	{
		super.paint(g);
		
		Player player = this.players.get(this.currentPlayer);
		
		Position old = player.getLastPosition();
		
		Point scrollOffset = new Point(getHorizontalScrollBar().getValue(), getVerticalScrollBar().getValue());
		
		if(old != null)
		{
			g.drawLine((int)(old.getX()-scrollOffset.getX()), (int)(old.getY()-scrollOffset.getY()), (int)(this.current.getX()-scrollOffset.getX()), (int)(this.current.getY()-scrollOffset.getY()));
		}
		
		for(Player p : players)
		{
			p.paint(g, scrollOffset);
		}
		
		int bobbleWidth = 10;
		
		if(this.current != null)
			g.fillOval((int)(this.current.getX() - (bobbleWidth/2) - scrollOffset.getX()), (int)(this.current.getY() - (bobbleWidth/2) - scrollOffset.getY()), bobbleWidth, bobbleWidth);
			
		// draw line between previous position and current
		// check collisions
		// check angle compared to old
	}
}

public class ColourQueue
{
	private Stack<Color> colours;
	
	public ColourQueue()
	{
		this.colours = new Stack<Color>();
		
		this.colours.push(Color.LIGHT_GRAY);
		this.colours.push(Color.GRAY);
		this.colours.push(Color.DARK_GRAY);
		this.colours.push(Color.WHITE);
		this.colours.push(Color.PINK);
		this.colours.push(Color.ORANGE);
		this.colours.push(Color.BLACK);
		this.colours.push(Color.CYAN);
		this.colours.push(Color.MAGENTA);
		this.colours.push(Color.YELLOW);
		this.colours.push(Color.BLUE);
		this.colours.push(Color.GREEN);
		this.colours.push(Color.RED);
	}
	
	public Color get()
	{
		return this.colours.pop();
	}
}

}
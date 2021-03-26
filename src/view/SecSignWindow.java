package view;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import environment.Environment;
import environment.InterfaceAgentRobot;
import robot.Action;

public class SecSignWindow extends SlaveView implements MouseListener{

	private static final long serialVersionUID = 1L;
	private static final int offset_top_border = 27;

	private SecSignPane panel;
	private PrimSignWindow primWindow;

	public SecSignWindow(String name, PrimSignWindow prim) {
		super(name);

		primWindow = prim;
		primWindow.addMouseListener(this);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// setting starting screen size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int window_base_height = (int) (screenSize.getHeight() * 0.5);
		int window_base_width = (int) (screenSize.getWidth() * 0.9);
		this.setBounds(0, 0, window_base_width, window_base_height);

		// creating pane
		panel = new SecSignPane(primWindow.getTopOffset());
		this.setContentPane(panel);

	}

	@Override
	public void setFocus(int ID, Environment env) {
		InterfaceAgentRobot agent = env.getInterface(ID);
		panel.setAgent(agent);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point location = e.getPoint(); 
		boolean tooLow = location.getY() > 
		(primWindow.getScreenHeight() * 2 
				+ primWindow.getTopOffset() 
				+ primWindow.getYOffset() 
				+ offset_top_border); 
		boolean tooFar = location.getX() > 
		(primWindow.getScreenWidth() * Action.values().length
				+ primWindow.getXOffset() * (Action.values().length -1));

		int y = (int) (location.getY() - offset_top_border - primWindow.getTopOffset());
		y %= primWindow.getScreenHeight() + primWindow.getYOffset();

		int x = (int) location.getX();
		x %= primWindow.getScreenWidth() + primWindow.getXOffset();

		if (y < 0 || y > primWindow.getScreenHeight() || tooLow || tooFar || x > primWindow.getScreenWidth()) {
			System.out.println("outside of screens!");
		} else {
			Point position = new Point(x, y);

			int line,column;
			Dimension sensorDimension = primWindow.getSensorsDimension();
			int pixelsPerLine = primWindow.getScreenHeight() / sensorDimension.height;
			int pixelsPerColumn = primWindow.getScreenWidth() / sensorDimension.width;

			line = Math.min(position.y / pixelsPerLine, sensorDimension.height-1);
			column = Math.min(position.x / pixelsPerColumn, sensorDimension.width-1);

			panel.setPositon(line * sensorDimension.width + column);
			panel.repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}

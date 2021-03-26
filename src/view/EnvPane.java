package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import environment.Direction;
import environment.Object;
import environment._2DMap;
import objects.Robot;

public class EnvPane extends JPanel{

	private static final long serialVersionUID = 1L;

	private _2DMap map;
	private int IDFocused;

	public EnvPane(_2DMap map_) {
		map = map_;
		IDFocused = 0;
	}

	@Override
	public void paintComponent(Graphics g){

		int map_height = map.getHeight();
		int map_width = map.getWidth();

		Dimension pane_size = this.getSize();
		g.setColor(Color.white);
		g.fillRect(0, 0, pane_size.width, pane_size.height);

		for (int line=0; line<map_height; line++) {
			for (int column=0; column<map_width; column++) {
				for (Object obj: map.getMap()[line][column]) {
					drawObject(g, obj, line, column, pane_size);
				}
			}
		}

		drawTrace(map.getRobot(IDFocused), g, pane_size);

	}

	private void drawObject(Graphics g2d, Object obj, int y_map, int x_map, Dimension pane_size) {
		int y_offset = pane_size.height / map.getHeight();
		int x_offset = pane_size.width / map.getWidth();

		int y_pane = y_offset * y_map;
		int x_pane = x_offset * x_map;

		if (obj.getImage() == null) {
			g2d.setColor(obj.getColor());
			g2d.fillRect(x_pane, y_pane, x_offset, y_offset);
		} else {
			BufferedImage image = (BufferedImage) obj.getImage();
			double angle = directionToAngle(obj.getDirection());

			AffineTransform backup = ((Graphics2D) g2d).getTransform();
			AffineTransform a = AffineTransform.getRotateInstance(Math.toRadians(angle), x_pane + x_offset/2, y_pane + y_offset/2);
			((Graphics2D) g2d).setTransform(a);
			if (angle == 90 || angle == 270) {
				g2d.drawImage(image, 
						x_pane + x_offset/2 - y_offset/2, 
						y_pane + y_offset/2 - x_offset/2, 
						y_offset, x_offset, null);
			} else {
				g2d.drawImage(image, 
						x_pane, 
						y_pane, 
						x_offset, y_offset, null);
			}
			((Graphics2D) g2d).setTransform(backup);
		}
	}

	private void drawTrace(Robot robot, Graphics g2d, Dimension pane_size) {
		int y_offset = (int)Math.floor(pane_size.width / map.getWidth());
		int x_offset = (int)Math.floor(pane_size.height / map.getHeight());

		g2d.setColor(new Color(robot.getId()*(16777216/map.getNbRobot())));
		Point buffer = null;
		for(Point nextPos: robot.getTrace()) {
			if (buffer != null) {
				g2d.drawLine((int)((buffer.x + 0.5)*y_offset), 
						(int) ((buffer.y+0.5)*x_offset), 
						(int) ((nextPos.x+0.5)*y_offset), 
						(int) ((nextPos.y+0.5)*x_offset));
			}
			buffer = nextPos;
		}
	}

	private double directionToAngle(Direction direction) {
		switch (direction) {
		case NORTH:
			return 0;
		case SOUTH:
			return 180;
		case WEST:
			return 270;
		case EAST:
			return 90;
		}
		return 0;
	}

	public void setFocus(int IDRobot) {
		IDFocused = IDRobot;
	}
}

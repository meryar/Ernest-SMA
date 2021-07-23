package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import agents.AgentDeveloppemental;
import environment.Direction;
import environment.Environment;
import environment.InterfaceAgentRobot;
import environment.Object;
import environment._2DMap;
import objects.Robot;
import robot.Action;
import useful.Pair;

public class EnvPane extends JPanel{

	private static final long serialVersionUID = 1L;

	private Environment env;
	private _2DMap map;
	private int IDFocused;

	public EnvPane(Environment env_) {
		env = env_;
		map = env.getMap();
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

		//drawTrace(map.getRobot(IDFocused), g, pane_size);
		
		if (((AgentDeveloppemental)env.getInterface(IDFocused).getAgent()).getRejectedPaths() != null){
			for (Pair<Pair<Point, Direction>, ArrayList<Action>> path: ((AgentDeveloppemental)env.getInterface(IDFocused).getAgent()).getRejectedPaths()) {
				drawTarget(env.getInterface(IDFocused), path, g, pane_size, Color.RED);
			}
		}
		
		drawTarget(env.getInterface(IDFocused), ((AgentDeveloppemental)env.getInterface(IDFocused).getAgent()).getCurrentPath(), g, pane_size, Color.GREEN);

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

		g2d.setColor(Color.BLACK);
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
	

	private void drawTarget(InterfaceAgentRobot inter, ArrayList<Action> path, Graphics g, Dimension pane_size, Color line_color) {
		ArrayList<Point> positions = new ArrayList<Point>();
		ArrayList<Action> actions = new ArrayList<Action>();
		ArrayList<Direction> directions = new ArrayList<Direction>();
		
		int y_offset = (int)Math.floor(pane_size.width / map.getWidth());
		int x_offset = (int)Math.floor(pane_size.height / map.getHeight());

		Point currentPos = inter.getRobot().getPosition();
		Direction currentDir = inter.getRobot().getDirection();
		
		for(Action act: path) {
			positions.add(currentPos);
			actions.add(act);
			directions.add(currentDir);
			
			switch (act) {
				case BUMP: 
				case EAT:
				case FEAST:
				case FIGHT:
					break;
				case MOVE_FWD:
					currentPos = map.getOrientedRelPos(currentPos, currentDir, new Point(0, -1));
					break;
				case ROTATE_LEFT:
					currentDir = inter.getRobot().rotatedDirection(currentDir, "left");
					break;
				case ROTATE_RIGHT:
					currentDir = inter.getRobot().rotatedDirection(currentDir, "right");
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + act);
			}
		}
		
		for (int i=0; i<positions.size(); i++) {
			Point pos = positions.get(i);
			Action act = actions.get(i);
			Direction dir = directions.get(i);
			
			int x = pos.x * y_offset;
			int y = pos.y * x_offset;
			
			g.setColor(switch (act) {
					case BUMP: {
						yield Color.green;
					}
					case EAT:{
						yield Color.BLUE;
					}
					case FIGHT:{
						yield Color.RED;
					}
					case FEAST:{
						yield Color.BLACK;
					}
					case MOVE_FWD:
					case ROTATE_LEFT:
					case ROTATE_RIGHT:{
						yield line_color;
					}
					default:
						throw new IllegalArgumentException("Unexpected value: " + act);
			});
			
			if (act == Action.BUMP || act == Action.EAT || act == Action.FIGHT || act == Action.FEAST) {
				g.drawOval(x + y_offset/4, y + x_offset/4, x_offset/2, y_offset/2);
			} else if (act == Action.MOVE_FWD) {
				Point next_pos = map.getOrientedRelPos(pos, dir, new Point(0,-1));
				g.drawLine(x + y_offset/2, y + x_offset/2, next_pos.x * y_offset + y_offset/2, next_pos.y * x_offset + x_offset/2);
			}
		}
		
	}

	private void drawTarget(InterfaceAgentRobot inter, Pair<Pair<Point, Direction>, ArrayList<Action>> path_,
			Graphics g, Dimension pane_size, Color line_color) {
		

		
		int y_offset = (int)Math.floor(pane_size.width / map.getWidth());
		int x_offset = (int)Math.floor(pane_size.height / map.getHeight());
		
		Point currentPos = path_.left().left();
		Direction currentDir = path_.left().right();
		
		g.setColor(Color.GRAY);
		g.fillRect((int)((currentPos.x + 0.4) * y_offset), (int)((currentPos.y + 0.4) * x_offset), x_offset/4, y_offset/4);
		
		ArrayList<Point> positions = new ArrayList<Point>();
		ArrayList<Action> actions = new ArrayList<Action>();
		ArrayList<Direction> directions = new ArrayList<Direction>();
		
		ArrayList<Action> path = path_.right();
		
		for(Action act: path) {
			positions.add(currentPos);
			actions.add(act);
			directions.add(currentDir);
			
			switch (act) {
				case BUMP: 
				case EAT:
				case FEAST:
				case FIGHT:
					break;
				case MOVE_FWD:
					currentPos = map.getOrientedRelPos(currentPos, currentDir, new Point(0, -1));
					break;
				case ROTATE_LEFT:
					currentDir = inter.getRobot().rotatedDirection(currentDir, "left");
					break;
				case ROTATE_RIGHT:
					currentDir = inter.getRobot().rotatedDirection(currentDir, "right");
					break;
				default:
					throw new IllegalArgumentException("Unexpected value: " + act);
			}
		}
		
		for (int i=0; i<positions.size(); i++) {
			Point pos = positions.get(i);
			Action act = actions.get(i);
			Direction dir = directions.get(i);
			
			int x = pos.x * y_offset;
			int y = pos.y * x_offset;
			
			g.setColor(switch (act) {
					case BUMP: {
						yield Color.green;
					}
					case EAT:{
						yield Color.BLUE;
					}
					case FIGHT:{
						yield Color.RED;
					}
					case FEAST:{
						yield Color.BLACK;
					}
					case MOVE_FWD:
					case ROTATE_LEFT:
					case ROTATE_RIGHT:{
						yield line_color;
					}
					default:
						throw new IllegalArgumentException("Unexpected value: " + act);
			});
			
			if (act == Action.BUMP || act == Action.EAT || act == Action.FIGHT || act == Action.FEAST) {
				g.drawOval(x + y_offset/4, y + x_offset/4, x_offset/2, y_offset/2);
			} else if (act == Action.MOVE_FWD) {
				Point next_pos = map.getOrientedRelPos(pos, dir, new Point(0,-1));
				g.drawLine(x + y_offset/2, y + x_offset/2, next_pos.x * y_offset + y_offset/2, next_pos.y * x_offset + x_offset/2);
			}
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

package environment;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Main;

public abstract class Object {

	private Color color;
	private Environment.Touch touch;
	protected String name;
	private boolean visible;
	private Image image;
	private Direction direction;
	protected _2DMap map;
	protected Point position;

	public Object(Color color_, Environment.Touch touch_, String name_, boolean visible_, _2DMap map_, Point position) {
		color = color_;
		touch = touch_;
		name = name_;
		visible = visible_;
		direction = Direction.NORTH;
		map = map_;
		setPosition(position);
	}

	public Object(String imageName, Color color, Environment.Touch touch, String name, boolean visible, Direction direction_, _2DMap map, Point position) {
		this(color, touch, name, visible, map, position);
		direction = direction_;

		try {
			image = ImageIO.read(new File("ressources/images/" + imageName));
		} catch (IOException e) {
			System.out.println("file " + imageName + " could not be found or read in ressources/images");
		}
	}

	public Environment.Touch getTouch() {return touch;}

	public Color getColor() {return color;}

	public Image getImage() {return image;}

	public String getName() {return name;}

	public Direction getDirection() {return direction;}

	public boolean getVisible() {return visible;}

	protected _2DMap getMap() {return map;}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public void setDirection(Direction newDir) {
		this.direction = newDir;
	}

	public void reset() {}

	public void move(Point newPosition) {
		map.moveObject(name, position, newPosition);
	}

	public void randomTeleport() {
		map.randomMove(name, position);
	}

	public void makeDisappear() {
		map.getMap()[position.y][position.x].remove(this);
	}
	
	public static void make_trace_creation(String object_type, String name, Point pos, String direction) {
		String file_name = Main.env_trace;
		
		try {
			File myObj = new File(file_name);
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("adding to trace.");
			}
			
			FileWriter myWriter = new FileWriter(file_name, true);
			
			myWriter.write("creation, " + name + ", " + object_type + ", " + pos + ", " + direction + "\n");
			
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	
	public static void make_trace_creation(String object_type, String name, Point pos) {
		make_trace_creation(object_type, name, pos, "NORTH");
	}
}

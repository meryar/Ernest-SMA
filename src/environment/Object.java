package environment;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class Object {
	
	private Color color;
	private Environment.Touch touch;
	private String name;
	private boolean visible;
	private Image image;
	private Direction direction;
	private _2DMap map;
	private Point position;
	
	public Object(Color color_, Environment.Touch touch_, String name_, boolean visible_, _2DMap map_, Point position) {
		color = color_;
		touch = touch_;
		name = name_;
		visible = visible_;
		direction = Direction.NORTH;
		map = map_;
	}
	
	public Object(String imageName, Color color, Environment.Touch touch, String name, boolean visible, Direction direction_, _2DMap map, Point position) {
		this(color, touch, name, visible, map, position);
		
		try {
		    image = ImageIO.read(new File("ressources/images/" + imageName));
		} catch (IOException e) {
			System.out.println("file " + imageName + " could not be found or read in ressources/images");
		}
		direction = direction_;
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
}

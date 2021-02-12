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
	
	public Object(Color color_, Environment.Touch touch_, String name_, boolean visible_) {
		color = color_;
		touch = touch_;
		name = name_;
		visible = visible_;
		direction = Direction.NORTH;
	}
	
	public Object(String imageName, Environment.Touch touch_, String name_, boolean visible_, Direction direction_) {
		
		direction = direction_;
		
		try {
		    image = ImageIO.read(new File("ressources/images/" + imageName));
		} catch (IOException e) {
			System.out.println("file " + imageName + " could not be found or read in ressources/images");
		}
		touch = touch_;
		name = name_;
		visible = visible_;
	}
	
	public Environment.Touch getTouch() {return touch;}

	public Color getColor() {return color;}
	
	public Image getImage() {return image;}
	
	public String getName() {return name;}
	
	public Direction getDirection() {return direction;}
}

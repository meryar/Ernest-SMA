package objects;

import java.awt.Color;
import java.awt.Point;

import environment.Direction;
import environment.Environment;
import environment.Object;
import environment._2DMap;

public class Block extends Object{
	
	public Block(_2DMap map, Environment.Touch touch, Color fieldColor, String name, boolean visible, Point position) {
		super(fieldColor, touch, name, visible, map, position);
	}
	
	public Block(_2DMap map, Color color, Environment.Touch touch, String imageName, String name, boolean visible, Direction direction, Point position) {
		super(imageName, color, touch, name, visible, direction, map, position);
	}

}

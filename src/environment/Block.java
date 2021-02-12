package environment;

import java.awt.Color;

public class Block extends Object{
	
	
	
	public Block(Environment.Touch touch, Color fieldColor, String name, boolean visible) {
		super(fieldColor, touch, name, visible);
		// TODO Auto-generated constructor stub
	}
	
	public Block(Environment.Touch touch, String imageName, String name, boolean visible, Direction direction) {
		super(imageName, touch, name, visible, direction);
		// TODO Auto-generated constructor stub
	}

}

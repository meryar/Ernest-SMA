package sensory_system;

import java.awt.Color;
import java.awt.Point;

import environment.Direction;
import environment.Robot;
import environment._2DMap;

public class VisualSensor extends Sensor<Color>{
	
	private Point relPos;

	public VisualSensor(Robot robot_, _2DMap map_, int xrel, int yrel) {
		super(robot_, map_);
		relPos = new Point(xrel,yrel);
	}

	@Override
	public Color getSensoryInformation() throws Exception{
		
		Direction robotOrientation = robot.getDirection();
		Point posSens = map.getOrientedRelPos(robot.getPosition(), robotOrientation, relPos);
		
		if (!map.isLegalPosition(posSens)) {return null;}
		
		return map.getMap()[posSens.y][posSens.x].get(0).getColor();
	}
	
	
	
}

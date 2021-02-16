package sensory_system;

import java.awt.Color;
import java.awt.Point;
import java.util.Vector;

import environment.Direction;
import environment._2DMap;
import objects.Robot;

public class VisualSensor extends Sensor<Color>{
	
	private Point relPos;

	public VisualSensor(Robot robot_, _2DMap map_, Point relativePos) {
		super(robot_, map_);
		relPos = relativePos;
	}

	@Override
	public Color getSensoryInformation(){
		
		Direction robotOrientation = robot.getDirection();
		Point posSens = map.getOrientedRelPos(robot.getPosition(), robotOrientation, relPos);
		
		if (!map.isLegalPosition(posSens)) {return null;}
		
		return map.getMap()[posSens.y][posSens.x].get(0).getColor();
	}
	
	
	
}

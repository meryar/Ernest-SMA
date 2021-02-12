package sensory_system;

import java.awt.Color;

import environment.Direction;
import environment.Environment;
import environment.Robot;
import environment._2DMap;

public class VisualSensor extends Sensor<Color>{
	
	private int xRel, yRel;

	public VisualSensor(Robot robot_, _2DMap map_, int xrel, int yrel) {
		super(robot_, map_);
		xRel = xrel;
		yRel = yrel;
	}

	@Override
	public Color getSensoryInformation() throws Exception{
		
		Direction robotOrientation = robot.getDirection();
		int posXSens,posYSens;
		switch (robotOrientation) {
			case NORTH:
				posYSens = robot.getPosition().y + yRel;
				posXSens = robot.getPosition().x + xRel;
				break;
			case SOUTH:
				posYSens = robot.getPosition().y - yRel;
				posXSens = robot.getPosition().x - xRel;
				break;
			case WEST:
				posYSens = robot.getPosition().y - xRel;
				posXSens = robot.getPosition().x + yRel;
				break;
			case EAST:
				posYSens = robot.getPosition().y + xRel;
				posXSens = robot.getPosition().x - yRel;
				break;
			default:
				throw new IllegalStateException("Robot" + robot.getName() + " facing unknown direction");
				
		}
		
		if (posXSens < 0 || posYSens < 0 || posXSens >= map.getWidth() || posYSens >= map.getHeight()) {return null;}
		
		return map.getMap()[posYSens][posXSens].get(0).getColor();
	}
	
	
	
}

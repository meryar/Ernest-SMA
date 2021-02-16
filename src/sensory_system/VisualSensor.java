package sensory_system;

import java.awt.Color;
import java.awt.Point;
import java.util.Vector;

import environment.Direction;
import environment._2DMap;
import objects.Robot;

public class VisualSensor extends Sensor<Color>{
	
	private Point relPos;
	Vector<Point> path;

	public VisualSensor(Robot robot_, _2DMap map_, Point relativePos) {
		super(robot_, map_);
		relPos = relativePos;
		path = makePath(relativePos);
	}

	@Override
	public Color getSensoryInformation(){
		
		Direction robotOrientation = robot.getDirection();
		Point posSens = map.getOrientedRelPos(robot.getPosition(), robotOrientation, relPos);
		
		if (!map.isLegalPosition(posSens)) {return null;}
		
		return map.getMap()[posSens.y][posSens.x].get(0).getColor();
	}
	
	private Vector<Point> makePath(Point relativePos) {
		Vector<Point> res = new Vector<Point>();
		double dx = relativePos.x;
		double dy = relativePos.y;
		boolean reverse = (dy < dx);
		double er = 0;
		double er10 = !reverse ? dy/dx : dx/dy;
		double er01 = -1;
		int sign = !reverse ? relativePos.x / Math.abs(relativePos.x) : relativePos.y / Math.abs(relativePos.y);
		
		if (!reverse) {
			for (int x = relativePos.x; x != 0; x-= 1*sign) {
				res.add(0, (Point) relativePos.clone());
				er += er10;
				if (er >= 0.5) {
					relativePos.y += 1*sign;
					er += er01;
				}
			}
		} else {
			for (int y = relativePos.y; y != 0; y-= 1*sign) {
				res.add(0, (Point) relativePos.clone());
				er += er10;
				if (er >= 0.5) {
					relativePos.x += 1*sign;
					er += er01;
				}
			}
		}
		
		
		return res;
	}
	
}

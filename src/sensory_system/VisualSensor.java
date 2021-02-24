package sensory_system;

import java.awt.Color;
import java.awt.Point;
import java.util.Vector;

import environment.Direction;
import environment.Environment;
import environment._2DMap;
import objects.Robot;

public class VisualSensor extends Sensor<Color>{
	
	private Point relPos;
	//Vector<Point> path;

	public VisualSensor(Robot robot_, _2DMap map_, Point relativePos, int id) {
		super(robot_, map_, id);
		relPos = relativePos;
		//System.out.println("alpha: rel pos = " + relPos);
		//path = makePath(relativePos);
		//System.out.println("beta: relPos = " + relPos);
	}

	@Override
	public Color getSensoryInformation(){
		Direction robotOrientation = robot.getDirection();
		Point posSens = map.getOrientedRelPos(robot.getPosition(), robotOrientation, relPos);
		if (!map.isLegalPosition(posSens)) {return null;}
		if (map.getMap()[posSens.y][posSens.x].isEmpty()) {return null;}
		
		Color res = map.getMap()[posSens.y][posSens.x].get(map.getMap()[posSens.y][posSens.x].size() - 1).getColor();
		if (res.equals(Environment.FIELD_COLOR)) {return null;}
		return res;
	}
	
	private Vector<Point> makePath(Point relativePos) {
		Vector<Point> res = new Vector<Point>();
		double dx = relativePos.x;
		double dy = relativePos.y;
		boolean reverse = (dy < dx);
		double er = 0;
		double er10;
		try {
			er10 = !reverse ? dy/dx : dx/dy;      
		} catch (Exception e) {
			er10 = 0;
		}
		double er01 = -1;
		int sign = (int) (!reverse ? Math.signum(relativePos.x) : Math.signum(relativePos.y));
		if (sign == 0) {sign = 1;}
		
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

	public boolean isAvailable() {
		return !(getSensoryInformation() == null);
	}
	
}

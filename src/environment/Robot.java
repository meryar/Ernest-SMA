package environment;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;


import robot.Action;
import robot.InteractionSec;
import sensory_system.Sensor;
import sensory_system.VisualSensor;


public class Robot extends Object{
	
	private Queue<Vector> trace;
	private int sensors_number;
	private Vector<Sensor> sensors;  
	private Point position;
	private _2DMap map;
	
	public Robot(_2DMap map_, Color color_, Environment.Touch touch_, String name_, 
			boolean visible_, int posX, int posY, double visionRange) {
		super(color_, touch_, name_, visible_);
		map = map_;
		trace = new LinkedList();
		position = new Point(posX,posY);
		
		sensors_number = 0;
		sensors = new Vector<Sensor>((int) (6*visionRange));
		for (int i= 0; i<2*visionRange; i++) {
			for (int j=0; j<2*visionRange; j++) {
				if (Math.sqrt(Math.pow(i-Math.floor(visionRange), 2) + Math.pow(j-Math.floor(visionRange), 2)) <= visionRange) {
					sensors.add(new VisualSensor(this, map, 
							i - (int) Math.floor(visionRange), 
							j - (int) Math.floor(visionRange)));
					sensors_number += 1;
				}
			}
		}
	}
	
	public Robot(_2DMap map_, String imageName, Environment.Touch touch_, String name_, 
			boolean visible_, Direction direction_ ,int posX, int posY, double visionRange) {
		super(imageName, touch_, name_, visible_, direction_);

		map = map_;
		trace = new LinkedList();
		position = new Point(posX,posY);
		
		sensors_number = 0;
		sensors = new Vector<Sensor>((int) (6*visionRange));
		for (int i= 0; i<2*visionRange; i++) {
			for (int j=0; j<2*visionRange; j++) {
				if (Math.sqrt(Math.pow(i-Math.floor(visionRange), 2) + Math.pow(j-Math.floor(visionRange), 2)) <= visionRange) {
					sensors.add(new VisualSensor(this, map, 
							i - (int) Math.floor(visionRange), 
							j - (int) Math.floor(visionRange)));
					sensors_number += 1;
				}
			}
		}
	}
	
	public InteractionSec act(Action command) {
		// TODO: execute command + return resulting interactions
		InteractionSec result = new InteractionSec(command, sensors_number);
		
		switch(command) {
			case MOVE_FWD:
				result.getPrimInter().setEnactedAction(move_fwd());
		}
		
		return result;
		
	}
	
	private Action move_fwd() {
		Point newPosition = (Point) position.clone();
		switch (getDirection()) {
		case NORTH:
			newPosition.y--;
		case SOUTH:
			newPosition.y++;
		case EAST:
			newPosition.x++;
		case WEST:
			newPosition.x--;
		}
		
		if (map.isBlocked(newPosition)) {return Action.BUMP;}
		
		if (map.isFood(newPosition)) {return eat(newPosition);}
		
		if (map.isBigFood(newPosition)) {return hunt(newPosition);}
		
		map.moveObject(getName(), position, newPosition);
		
		
		return null;
	}

	private Action hunt(Point newPosition) {
		Point[] helperPos = {new Point(-1,-1),new Point(0,-2), new Point(1,-1)};
		
		
		return null;
	}

	private Action eat(Point newPosition) {
		ArrayList<Object> target = (ArrayList<Object>) map.getTypedObjects(Environment.Touch.FOOD, newPosition);
		
		if (!target.isEmpty()) {
			map.randomMove(target.get(0).getName(), newPosition);
		} else {
			return Action.BUMP;
		}
		
		return Action.EAT;
	}

	public Point getPosition() {return position;}
	
}

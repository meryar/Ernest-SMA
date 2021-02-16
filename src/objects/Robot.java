package objects;

import java.awt.Color;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import environment.Direction;
import environment.Environment;
import environment.Environment.Touch;
import environment.Object;
import environment._2DMap;
import robot.Action;
import sensory_system.Sensor;
import sensory_system.VisualSensor;


public class Robot extends Object{
	
	private Queue<Vector> trace;
	private int sensors_number;
	private Vector<Sensor> sensors;  
	private Action pending;
	
	public Robot(_2DMap map_, Color color_, Environment.Touch touch_, String name_,
			boolean visible_, Point position, double visionRange) {
		super(color_, touch_, name_, visible_, map_, position);
		trace = new LinkedList<Vector>();
		
		sensors_number = 0;
		sensors = new Vector<Sensor>();
		for (int i= 0; i<2*visionRange; i++) {
			for (int j=0; j<2*visionRange; j++) {
				if (Math.sqrt(Math.pow(i-Math.floor(visionRange), 2) + Math.pow(j-Math.floor(visionRange), 2)) <= visionRange) {
					sensors.add(new VisualSensor(this, getMap(), 
							i - (int) Math.floor(visionRange), 
							j - (int) Math.floor(visionRange)));
					sensors_number += 1;
				}
			}
		}
	}
	
	public Robot(_2DMap map_, Color color, String imageName, Environment.Touch touch_, String name_,
			boolean visible_, Direction direction_ , Point position, double visionRange) {
		super(imageName, color, touch_, name_, visible_, direction_, map_, position);

		trace = new LinkedList<Vector>();
		
		sensors_number = 0;
		sensors = new Vector<Sensor>((int) (6*visionRange));
		for (int i= 0; i<2*visionRange; i++) {
			for (int j=0; j<2*visionRange; j++) {
				if (Math.sqrt(Math.pow(i-Math.floor(visionRange), 2) + Math.pow(j-Math.floor(visionRange), 2)) <= visionRange) {
					sensors.add(new VisualSensor(this, getMap(), 
							i - (int) Math.floor(visionRange), 
							j - (int) Math.floor(visionRange)));
					sensors_number += 1;
				}
			}
		}
	}
	
	public int getSensorNb() {return sensors_number;}

	public void prepareAction(Action command) {
		pending = command;
		
		switch(command) {
		case MOVE_FWD:
			Point newPos = getMap().getOrientedRelPos(getPosition(), getDirection(), new Point(0,1));
			
			if (getMap().isLegalPosition(newPos) && getMap().isFood(newPos)) {
				List<Object> targets = getMap().getTypedObjects(Environment.Touch.FOOD, newPos);
				for (Object fish: targets) {
					if (((Fish) fish).get_attacked(getName())) {break;}
				}
			};
		case ROTATE_LEFT:
			break;
		case ROTATE_RIGHT:
			break;
		default:
			throw new IllegalStateException("Illegal command from agent: " + command);
		}
		
	}

	public Action getResults() {
		
		switch(pending) {
		case MOVE_FWD:
			Point newPos = getMap().getOrientedRelPos(getPosition(), getDirection(), new Point(0,-1));
			if (getMap().isLegalPosition(newPos)) {
				if (getMap().isHard(newPos)) {
					return Action.BUMP;
				} else if (getMap().isFood(newPos)) {
					List<Object> targets = getMap().getTypedObjects(Environment.Touch.FOOD, newPos);
					for (Object fish: targets) {
						if (((Fish) fish).was_eaten_by(getName())) {
							return ((Fish) fish).getAffordedOnDeath();
						}
					}
					return Action.FIGHT;
				} else {
					getMap().moveObject(getName(), getPosition(), newPos);
					return Action.MOVE_FWD;
				}
			} else {
				throw new IllegalStateException("Robot " + getName() + " is trying to exit map!");
			}
		case ROTATE_LEFT:
			break;
		case ROTATE_RIGHT:
			break;
		default:
			throw new IllegalStateException("Illegal command pending: " + pending);
		}
		
		return null;
	}
}

package objects;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import environment.Direction;
import environment.Environment;
import environment.Object;
import environment._2DMap;
import main.Main;
import robot.Action;
import sensory_system.Sensor;
import sensory_system.VisualSensor;


public class Robot extends Object{

	private List<Point> trace;
	private int sensors_number;
	private Vector<Sensor<Color>> sensors;  
	private Action pending;
	private int nbColors;

	public Robot(_2DMap map_, Color color_, Environment.Touch touch_, String name_,
			boolean visible_, Point position, double visionRange) {
		super(color_, touch_, name_, visible_, map_, position);
		trace = new ArrayList<Point>();
		nbColors = Main.colors.length;

		sensors_number = 0;
		sensors = new Vector<Sensor<Color>>();
		for (int i= 0; i<2*visionRange; i++) {
			for (int j=0; j<2*visionRange; j++) {
				Point location = new Point(i - (int) Math.floor(visionRange), j - (int) Math.floor(visionRange));
				sensors.add(new VisualSensor(this, getMap(), location, sensors_number));
				sensors_number += 1;
			}
		}
	}

	public Robot(_2DMap map_, Color color, String imageName, Environment.Touch touch_, String name_,
			boolean visible_, Direction direction_ , Point position, double visionRange) {
		super(imageName, color, touch_, name_, visible_, direction_, map_, position);

		trace = new ArrayList<Point>();
		nbColors = Main.colors.length;

		sensors_number = 0;
		sensors = new Vector<Sensor<Color>>();
		for (int i= 0; i<2*visionRange + 1; i++) {
			for (int j=0; j<2*visionRange + 1; j++) {
				Point location = new Point(j - (int) Math.floor(visionRange), i - (int) Math.floor(visionRange));
				sensors.add(new VisualSensor(this, getMap(), location, sensors_number));
				sensors_number += 1;
			}
		}
	}

	public int getSensorNb() {return sensors_number;}

	public void prepareAction(Action command) {
		pending = command;

		switch(command) {
		case MOVE_FWD:
			Point newPos = getMap().getOrientedRelPos(getPosition(), getDirection(), new Point(0,-1));
			if (getMap().isLegalPosition(newPos) && getMap().isFood(newPos)) {
				List<Object> targets = getMap().getTypedObjects(Environment.Touch.FOOD, newPos);
				for (Object fish: targets) {
					if (fish instanceof OrientedFish) {
						if (((OrientedFish) fish).get_attacked(getName(), getDirection())) {break;}
					} else if (fish instanceof Fish) {
						if (((Fish) fish).get_attacked(getName())) {break;}
					}
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
						if (fish instanceof OrientedFish) {
							if (((OrientedFish) fish).was_eaten_by(getName(), getDirection())) {
								return ((OrientedFish) fish).getAffordedOnDeath();
							}
						} else if (fish instanceof Fish) {
							if (((Fish) fish).was_eaten_by(getName())) {
								return ((Fish) fish).getAffordedOnDeath();
							}
						}
					}
					return Action.FIGHT;
				} else {
					move(newPos);
					return Action.MOVE_FWD;
				}
			} else {
				throw new IllegalStateException("Robot " + getName() + " is trying to exit map!");
			}
		case ROTATE_LEFT:
			setDirection(rotatedDirection(getDirection(), "left"));
			return Action.ROTATE_LEFT;
		case ROTATE_RIGHT:
			setDirection(rotatedDirection(getDirection(), "right"));
			return Action.ROTATE_RIGHT;
		default:
			throw new IllegalStateException("Illegal command pending: " + pending);
		}
	}

	private Direction rotatedDirection(Direction direction, String side) {

		return switch (side) {
		case "left":
			yield (switch (direction) {
			case NORTH:
				yield (Direction.WEST);
			case WEST:
				yield (Direction.SOUTH);
			case SOUTH:
				yield (Direction.EAST);
			case EAST:
				yield (Direction.NORTH);
			});
		case "right":
			yield (switch (direction) {
			case NORTH:
				yield (Direction.EAST);
			case EAST:
				yield (Direction.SOUTH);
			case SOUTH:
				yield (Direction.WEST);
			case WEST:
				yield (Direction.NORTH);
			});

		default:
			throw new IllegalStateException("can only rotate 'left' or 'right' and not: " + side);
		};
	}

	public Vector<Boolean> getSensoryInformation(){
		Vector<Boolean> res = new Vector<Boolean>();
		res.setSize((nbColors + Direction.values().length - 1) * getSensorNb());
		res.replaceAll(e -> false);

		for(int i=0; i<sensors_number; i++) {
			if (((VisualSensor)sensors.get(i)).isAvailable()) {
				Color seen = ((VisualSensor)sensors.get(i)).getSensoryInformation();
				if (!seen.equals(Environment.ROBOT_COLOR)) {
					res.set(getColorId(seen) * sensors_number + sensors.get(i).getId(), true);
				} else {
					Direction dir = ((VisualSensor)sensors.get(i)).getDirectionSeen();
					res.set((getColorId(seen) + getDirectionId(dir)) * sensors_number + sensors.get(i).getId(), true);
				}
			}
		}
		return res;
	}

	private int getDirectionId(Direction dir) {
		for (int i=0; i<Direction.values().length; i++) {
			if (Direction.values()[i].equals(dir)) {return i;}
		}
		System.out.println("Error: unknown direction seen by " + getName() + ": " + dir);
		return -1;
	}

	private int getColorId(Color seen) {
		for (int i=0; i<Main.colors.length; i++) {
			if (Main.colors[i].equals(seen)) {return i;}
		}
		System.out.println("Error: unknown color seen by " + getName() + ": " + seen);
		return -1;
	}

	@Override
	public void move(Point newPosition) {
		trace.add((Point)newPosition.clone());
		if (trace.size() > Main.trace_size) {
			trace.remove(0);
		}
		getMap().moveObject(getName(), getPosition(), newPosition);
	}

	public List<Point> getTrace(){
		return trace;
	}

	public int getId() {
		String name = getName();
		return Integer.parseInt(String.valueOf(name.charAt(name.length()-1)));
	}
}

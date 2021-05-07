package objects;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import environment.Direction;
import environment.Environment;
import environment._2DMap;
import robot.Action;

public class OrientedFish extends environment.Object {

	public static enum on_death{
		DISSAPEAR,
		RESPAWN_ELSEWHERE,
		UNDYING
	}

	private Map<Direction, String> hunters;
	private boolean eaten;
	on_death respawn;
	private Action afforded_on_death;
	private Vector<Direction> winners;

	public OrientedFish(String name, boolean visible, Direction direction, _2DMap map, Point position, 
			on_death resp_cond, Action reward_for_killing) {
		super("big_fish.png", Environment.FISH2, Environment.Touch.FOOD, name, visible, direction, map, position);

		hunters = new HashMap<Direction, String>();
		eaten = false;
		respawn = resp_cond;
		afforded_on_death = reward_for_killing;
		winners = new Vector<Direction>();
	}

	public boolean get_attacked(String hunter_name, Direction dir) {
		if (eaten) {return false;}
		else {
			hunters.put(dir, hunter_name);
			if (isFull()) {
				eaten = true;
			}
			return true;
		}
	}

	private boolean isFull() {
		if (hunters.containsKey(Direction.EAST) && hunters.containsKey(Direction.WEST)){
			winners.add(Direction.EAST);
			winners.add(Direction.WEST);
			return true;
		} else if (hunters.containsKey(Direction.NORTH) && hunters.containsKey(Direction.SOUTH)) {
			winners.add(Direction.NORTH);
			winners.add(Direction.SOUTH);
			return true;
		}
		return false;
	}

	public boolean was_eaten_by(String hunter_name, Direction dir) {
		if (eaten && winners.contains(dir)) {
			winners.remove(dir);
			if (winners.size() == 0) {die();}
			return true;
		} else {return false;}
	}

	@Override
	public void reset() {
		hunters.clear();
		winners.clear();
		eaten = false;
	}
	
	@Override
	public void randomTeleport() {
		map.randomIsolatedMove(name, position);
	}

	private void die() {
		switch (respawn) {
		case DISSAPEAR: 
			makeDisappear();
			break;
		case RESPAWN_ELSEWHERE:
			reset();
			randomTeleport();
			break;
		case UNDYING:
			reset();
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + respawn);
		}
	}

	public Action getAffordedOnDeath() {
		return afforded_on_death;
	}

}

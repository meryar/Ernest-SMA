package objects;

import java.awt.Color;
import java.awt.Point;
import java.util.Vector;

import environment.Direction;
import environment.Environment;
import environment.Object;
import environment._2DMap;
import robot.Action;
import environment.Environment.Touch;

public class Fish extends Object{
	
	public enum on_death{
		DISSAPEAR,
		RESPAWN_ELSEWHERE,
		UNDYING
	}
	
	private Vector<String> hunters;
	private int huntersMaxNb;
	private boolean eaten;
	on_death respawn;
	private Action afforded_on_death;
	
	public Fish(Color color_, Touch touch_, String name_, boolean visible_, _2DMap map, Point position, 
			int huntMax, on_death resp_cond, Action reward_for_killing) {
		super(color_, touch_, name_, visible_, map, position);
		
		huntersMaxNb = huntMax;
		hunters = new Vector<String>();
		eaten = false;
		respawn = resp_cond;
		afforded_on_death = reward_for_killing;
	}
	
	public Fish(String imageName, Color color, Environment.Touch touch_, String name_, boolean visible_, Direction direction_, _2DMap map, Point position,
			int huntMax, on_death resp_cond, Action reward_for_killing) {
		super(imageName, color, touch_, name_, visible_, direction_, map, position);
		
		huntersMaxNb = huntMax;
		hunters = new Vector<String>();
		eaten = false;
		respawn = resp_cond;
		afforded_on_death = reward_for_killing;
	}
	
	public boolean get_attacked(String hunter_name) {
		if (eaten) {return false;}
		else {
			hunters.add(hunter_name);
			if (hunters.size() == huntersMaxNb) {
				eaten = true;
			}
			return true;
		}
	}
	
	public boolean was_eaten_by(String hunter_name) {
		if (eaten && hunters.contains(hunter_name)) {
			hunters.remove(hunter_name);
			if (hunters.size() == 0) {die();}
			return true;
		} else {return false;}
	}
	
	@Override
	public void reset() {
		hunters.clear();
		eaten = false;
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
		default:
			throw new IllegalArgumentException("Unexpected value: " + respawn);
		}
	}
	
	public Action getAffordedOnDeath() {
		return afforded_on_death;
	}
	
}

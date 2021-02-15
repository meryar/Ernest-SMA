package objects;

import java.awt.Color;
import java.awt.Point;
import java.util.Vector;

import environment.Direction;
import environment.Environment;
import environment.Object;
import environment._2DMap;
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
	
	public Fish(Color color_, Touch touch_, String name_, boolean visible_, _2DMap map, Point position, 
			int huntMax) {
		super(color_, touch_, name_, visible_, map, position);
		// TODO Auto-generated constructor stub
	}
	
	public Fish(String imageName, Color color, Environment.Touch touch_, String name_, boolean visible_, Direction direction_, _2DMap map, Point position,
			int huntMax, on_death resp_cond) {
		super(imageName, color, touch_, name_, visible_, direction_, map, position);
		
		huntersMaxNb = huntMax;
		hunters = new Vector<String>();
		eaten = false;
		respawn = resp_cond;
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
	
	public void reset() {
		hunters.clear();
		eaten = false;
	}

	private void die() {
		switch (respawn) {
		case DISSAPEAR: 
			this.getMap().getMap()[getPosition().y][getPosition().x].remove(this);
			break;
		case RESPAWN_ELSEWHERE:
			reset();
			getMap().randomMove(getName(), getPosition());
			break;
		case UNDYING:
			reset();
		default:
			throw new IllegalArgumentException("Unexpected value: " + respawn);
		}
		
	}
	
}

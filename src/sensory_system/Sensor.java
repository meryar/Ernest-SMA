package sensory_system;

import environment._2DMap;
import objects.Robot;

public abstract class Sensor<T> {
	
	protected Robot robot;
	protected _2DMap map;
	
	public Sensor(Robot robot_, _2DMap map_) {
		robot = robot_;
		map = map_;
	}
	
	abstract T getSensoryInformation() throws Exception; 
	
}

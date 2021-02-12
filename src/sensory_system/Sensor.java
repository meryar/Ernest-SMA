package sensory_system;

import environment.Environment;
import environment.Robot;
import environment._2DMap;

public abstract class Sensor<T> {
	
	protected Robot robot;
	protected _2DMap map;
	
	public Sensor(Robot robot_, _2DMap map_) {
		robot = robot_;
		map = map_;
	}
	
	abstract T getSensoryInformation() throws Exception; 
	
}

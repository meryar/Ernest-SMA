package agents;

import environment.Robot;

public abstract class Agent {
	
	protected Robot robot;
	
	public Agent(Robot robot_) {
		robot = robot_;
	}

	public abstract void act();

	public abstract void getResults();

}

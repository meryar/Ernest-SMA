package agents;

import objects.Robot;

public abstract class Agent {
	
	protected Robot robot;
	
	public Agent(Robot robot_) {
		robot = robot_;
	}

	public abstract void commandRobot();

	public abstract void getResults();

}

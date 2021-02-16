package agents;

import objects.Robot;
import robot.Action;
import robot.InteractionSec;

public class AgentDeveloppemental extends Agent{
	
	InteractionSec last_inter;

	public AgentDeveloppemental(Robot robot_) {
		super(robot_);
		
	}

	@Override
	public void commandRobot() {
		Action intended = decideAction();
		last_inter = new InteractionSec(intended, robot.getSensorNb());
		robot.prepareAction(intended);
	}

	private Action decideAction() {
		// TODO: decision system
		return Action.MOVE_FWD;
	}

	@Override
	public void getResults() {
		// TODO Auto-generated method stub
		
	}

}

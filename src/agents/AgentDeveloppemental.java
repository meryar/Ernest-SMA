package agents;

import java.util.Vector;

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
		Action enacted = robot.getResults();
		
		// TODO: learn from result
		Vector<Boolean> sight = robot.getSensoryInformation();
	}

}

package environment;

import java.util.Collections;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import agents.Agent;
import agents.AgentDeveloppemental;
import jdk.internal.org.jline.reader.Widget;
import objects.Robot;
import robot.Action;

public class InterfaceAgentRobot {

	private Agent agent;
	private Robot robot;
	private Action lastEnacted;
	private Vector<Boolean> lastSeen;
	
	public InterfaceAgentRobot(Robot rob) {
		robot = rob;
		int input_size = 100;
		int output_size = 10;
		agent = new AgentDeveloppemental(input_size, output_size);
	}

	public void commandRobot() {
		Action intended = agent.decide(entryForDeciding());
		switch (intended) {
		case BUMP:
		case EAT:
		case FEAST:
		case FIGHT:
		case MOVE_FWD:
			robot.prepareAction(Action.MOVE_FWD);
			break;
		case ROTATE_LEFT:
			robot.prepareAction(Action.ROTATE_LEFT);
			break;
		case ROTATE_RIGHT:
			robot.prepareAction(Action.ROTATE_RIGHT);
			break;
		}
	}

	public void getResults() {
		Action enacted = robot.getResults();
	}
	
	private Vector<Float> entryForDeciding(){
		Vector<Float> res = new Vector();
		
		// first we add the secondary interaction's information (the sight)
		Vector<Boolean> perception = robot.getSensoryInformation();
		for (Action act: Action.values()) {
			Vector<Boolean> inter = (Vector<Boolean>) perception.clone();
			if (lastEnacted != act) {
				inter.replaceAll(b -> false);
			}
			//res.addAll(inter.stream().map(b -> b ? 1 : 0).collect(Collectors.toList()));
		}
		return null;
	}
	
}

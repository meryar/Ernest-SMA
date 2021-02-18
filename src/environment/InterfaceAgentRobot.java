package environment;

import java.util.Collections;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import agents.Agent;
import agents.AgentDeveloppemental;
import main.Main;
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
		lastEnacted = robot.getResults();
		lastSeen = robot.getSensoryInformation();
	}
	
	private Vector<Float> entryForDeciding(){
		Vector<Float> res = new Vector<>();
		Vector<Float> enact = new Vector<>();
		
		// first we add the secondary interaction's information (the sight)
		for (Action act: Action.values()) {
			Vector<Boolean> inter;
			if (lastSeen != null) {
				inter = (Vector<Boolean>) lastSeen.clone();
			} else {
				inter = new Vector<Boolean>();
				for (int s=0; s<robot.getSensorNb(); s++) {
					for (int c=0; c<Main.nb_colors; c++) {
						inter.add(false);
					}
				}
			}
			if (lastEnacted != act) {
				inter.replaceAll(b -> false);
				enact.add((float)0.0);
			} else {
				enact.add((float)1.0);
			}
			res.addAll(inter.stream().map(b -> b ? (float)1.0 : (float)0.0).collect(Collectors.toList()));
		}
		// then we add the primary interaction (touch)
		res.addAll(enact);
		System.out.println(res.size());
		return res;
	}
	
}

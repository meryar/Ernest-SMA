package environment;

import java.util.Collections;
import java.util.Vector;
import agents.Agent;
import agents.AgentDeveloppemental;
import main.Main;
import objects.Robot;
import robot.Action;

public class InterfaceAgentRobot {

	private Agent agent;
	private Robot robot;
	private Action lastEnacted, lastIntended;
	private Vector<Boolean> lastSeen;

	public InterfaceAgentRobot(Robot rob) {
		robot = rob;
		int input_size = ((Main.colors.length + Direction.values().length - 1) * robot.getSensorNb() + 1) * Action.values().length; 
		agent = new AgentDeveloppemental(input_size, Action.values().length);
	}

	public InterfaceAgentRobot(Robot rob, String file_name) {
		robot = rob;
		int input_size = ((Main.colors.length + Direction.values().length - 1) * robot.getSensorNb() + 1) * Action.values().length; 
		agent = new AgentDeveloppemental(input_size, Action.values().length, file_name);
	}

	public void commandRobot() {
		lastSeen = robot.getSensoryInformation();
		lastIntended = agent.decide(entryForDeciding());
		switch (lastIntended) {
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

		agent.learn(entryForLearning());
	}

	@SuppressWarnings("unchecked")
	private float[] entryForDeciding(){
		float[] res = new float[Action.values().length * robot.getSensorNb() 
		                        * (Main.colors.length + Direction.values().length - 1) 
		                        + Action.values().length];
		// first we add the secondary interaction's information (the sight)
		for (int i=0; i<Action.values().length; i++) {
			Vector<Boolean> inter;
			if (lastSeen != null) {
				inter = (Vector<Boolean>) lastSeen.clone();
			} else {
				inter = new Vector<Boolean>();
				inter.setSize(robot.getSensorNb() * (Main.colors.length + Direction.values().length - 1) );
				Collections.fill(inter, false);
			}
			if (lastEnacted != Action.values()[i]) {
				Collections.fill(inter, false);
				res[res.length - Action.values().length + i] = 0;
			} else {
				res[res.length - Action.values().length + i] = 1;
			}
			for (int j=0; j<inter.size(); j++) {
				res[i*inter.size() + j] = inter.get(j)? 1: 0;
			}
		}
		return res;
	}

	private float[] entryForLearning() {
		int codeEnacted = -1;
		float[] base = entryForDeciding();
		Vector<Action> alternates = new Vector<Action>();
		alternates.add(Action.MOVE_FWD);
		alternates.add(Action.BUMP);
		alternates.add(Action.FIGHT);
		alternates.add(Action.EAT);
		alternates.add(Action.FEAST);

		if (alternates.contains(lastEnacted)) {
			for (int i=0; i<alternates.size(); i++) {
				if (lastEnacted != alternates.get(i)) {
					base[base.length - Action.values().length + i] = -1f;
				} 
			}
		}

		for (int i = 0; i < Action.values().length; i++) {
			if (Action.values()[i].equals(lastEnacted)) {
				codeEnacted = i;
			}
		}

		int offset = base.length / Action.values().length -1;
		for (int i=0; i<offset; i++) {
			if (base[codeEnacted * offset + i] == 0f) {
				base[codeEnacted * offset + i] = -1f;
			}
		}
		return base;
	}

	public Robot getRobot() {
		return robot;
	}

	public Agent getAgent() {
		return agent;
	}

	public void save(String file_name) {
		((AgentDeveloppemental) agent).save(file_name);
	}
	
	public void updateNeurons() {
		((AgentDeveloppemental)agent).updateNeurons();
	}

}

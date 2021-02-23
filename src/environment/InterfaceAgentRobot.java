package environment;

import java.util.Vector;
import java.util.stream.Collectors;

import agents.Agent;
import agents.AgentDeveloppemental;
import main.Main;
import objects.Robot;
import robot.Action;
import view.RobotView;

public class InterfaceAgentRobot {

	private Agent agent;
	private Robot robot;
	private Action lastEnacted, lastIntended;
	private Vector<Boolean> lastSeen;
	private RobotView view;
	
	public InterfaceAgentRobot(Robot rob) {
		robot = rob;
		int input_size = (Main.colors.length * robot.getSensorNb() + 1) * Action.values().length; 
		agent = new AgentDeveloppemental(input_size);
		
		view = new RobotView("Robot " + robot.getId(), robot.getSensorNb());
		view.pack();
        view.setVisible(true);
	}

	public void commandRobot() {
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
		//agent.learn(entryForDeciding(), entryForLearning());
		view.updateView(entryForDeciding());
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
					for (int c=0; c<Main.colors.length; c++) {
						inter.add(false);
					}
				}
			}
			if (lastEnacted != act) {
				inter.replaceAll(b -> false);
				enact.add(0f);
			} else {
				enact.add(1f);
			}
			res.addAll(inter.stream().map(b -> b ? 1f : 0f).collect(Collectors.toList()));
		}
		// then we add the primary interaction (touch)
		res.addAll(enact);

		return res;
	}
	
	private Vector<Float> entryForLearning() {
		Vector<Float> res = new Vector<>();
		Vector<Action> alternates = new Vector<Action>();
		alternates.add(Action.MOVE_FWD);
		alternates.add(Action.BUMP);
		alternates.add(Action.FIGHT);
		alternates.add(Action.EAT);
		alternates.add(Action.FEAST);
		
		for (Action act: Action.values()) {
			res.add(act == lastEnacted? 1f: 0f);
		}
		
		if (alternates.contains(lastEnacted)) {
			for (int i=0; i<alternates.size(); i++) {
				if (lastEnacted != alternates.get(i)) {
					res.set(i, -1f);
				}
			}
		}
		
		return res;
	}
	
}

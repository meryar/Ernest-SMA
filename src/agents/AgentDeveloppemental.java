package agents;

import java.util.Vector;

import agent_developpemental.Perceptron;
import main.Main;
import objects.Robot;
import robot.Action;
import robot.InteractionSec;

public class AgentDeveloppemental extends Agent{
	
	Perceptron perceptron;

	public AgentDeveloppemental(int input_size, int output_size) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Action decide(Vector<Float> resultsTMoins1) {
		Action intended = decideAction();
		//last_inter = new InteractionSec(intended, robot.getSensorNb());
		return intended;
	}

	private Action decideAction() {
		
		//Vector<Double> predictions
		
		return Action.MOVE_FWD;
	}

	@Override
	public void learn(Vector<Float> results) {
		//Action enacted = robot.getResults();
		
		// TODO: learn from result
		//Vector<Boolean> sight = robot.getSensoryInformation();
	}

}

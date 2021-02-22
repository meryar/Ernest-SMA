package agents;

import java.util.Vector;import agent_developpemental.Neuron;
import agent_developpemental.Perceptron;
import main.Main;
import objects.Robot;
import robot.Action;
import robot.InteractionSec;

public class AgentDeveloppemental extends Agent{
	
	private static final float certitude_treshold = 0.8f;
	
	private Perceptron perceptron;

	public AgentDeveloppemental(int input_size) {
		perceptron = new Perceptron(input_size, input_size);
	}

	@Override
	public Action decide(Vector<Float> resultsTMinus1) {
		Action intended = decideAction(resultsTMinus1);
		//last_inter = new InteractionSec(intended, robot.getSensorNb());
		return intended;
	}

	private Action decideAction(Vector<Float> resultsTMinus1) {
		
		Vector<Float> predictions = perceptron.compute(resultsTMinus1);
		
		System.out.println(predictions);
		
		return Action.MOVE_FWD;
	}

	@Override
	public void learn(Vector<Float> results) {
		
		// TODO: learn from result
		//Vector<Boolean> sight = robot.getSensoryInformation();
	}

}

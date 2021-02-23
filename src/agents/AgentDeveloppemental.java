package agents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;import agent_developpemental.Neuron;
import agent_developpemental.Perceptron;
import main.Main;
import objects.Robot;
import robot.Action;
import robot.InteractionSec;

public class AgentDeveloppemental extends Agent{
	
	private static final float certitude_treshold = 0.8f;
	
	private Map<Action,Float> utilities;
	
	private Perceptron perceptron;

	public AgentDeveloppemental(int input_size) {
		perceptron = new Perceptron(input_size, input_size);
		utilities = new HashMap<>();
		utilities.put(Action.MOVE_FWD, 5f);
		utilities.put(Action.BUMP, -10f);
		utilities.put(Action.FIGHT, -20f);
		utilities.put(Action.EAT, 50f);
		utilities.put(Action.FEAST, 200f);
		utilities.put(Action.ROTATE_LEFT, -3f);
		utilities.put(Action.ROTATE_RIGHT, -3f);
	}

	@Override
	public Action decide(Vector<Float> resultsTMinus1) {
		Action intended = decideAction(resultsTMinus1);
		//last_inter = new InteractionSec(intended, robot.getSensorNb());
		return intended;
	}

	private Action decideAction(Vector<Float> resultsTMinus1) {
		// get predictions of success from perceptron
		
		Vector<Float> predictions = perceptron.compute(resultsTMinus1);
		//System.out.println(predictions);
		Action choice;
		
		// find the most uncertain prediction
		float min = 1f;
		for (float pred: predictions) {
			if (Math.abs(pred) < min) {
				min = Math.abs(pred);
			}
		}
		
		int nb_sensors = (int) Math.pow(2*Main.robot_vision_range + 1, 2);
		// if one prediction is under the certitude threshold
		if (min < certitude_treshold) {
			int min_index = predictions.indexOf(min);
			if (min_index < Action.values().length * Main.colors.length * nb_sensors) {
				choice = Action.values()[(int) Math.floor(min_index / (Main.colors.length * nb_sensors))];
			} else {
				choice = Action.values()[min_index - Action.values().length * Main.colors.length * nb_sensors];
			}
		} else {
			choice = mostUseful(predictions);
		}
		
		return choice;
	}

	private Action mostUseful(Vector<Float> predictions) {
		float max_utility = Float.MIN_VALUE;
		int max_index = -1;

		List<Float> to_consider = predictions.subList(Action.values().length * Main.colors.length * (int) Math.pow(2*Main.robot_vision_range + 1, 2), predictions.size());

		for (int i=0; i<to_consider.size(); i++) {
			if (to_consider.get(i) > certitude_treshold) {
				if (utilities.get(Action.values()[i]) > max_utility) {
					max_utility = utilities.get(Action.values()[i]);
					max_index = i;
				}
			}
		}
		
		assert (max_index >= 0 && max_index < Action.values().length): "error in best movement calculation";
		return Action.values()[max_index];
	}

	@Override
	public void learn(Vector<Float> results) {
		
		// TODO: learn from result
		//Vector<Boolean> sight = robot.getSensoryInformation();
	}

}

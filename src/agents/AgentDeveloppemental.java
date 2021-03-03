package agents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import agent_developpemental.Data;
import agent_developpemental.Perceptron;
import main.Main;
import robot.Action;

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
	public Action decide(Data resultsTMinus1) {
		
		return decideAction(resultsTMinus1);
	}

	private Action decideAction(Data resultsTMinus1) {
		Action choice;
		
		// get predictions of success from perceptron
		Vector<Float> prediction = perceptron.compute(resultsTMinus1.getData());
		Data predictions = new Data(Action.values().length, 
				Main.colors.length, 
				(int) (Math.pow(Main.robot_vision_range*2 +1, 2)),
				prediction);
		
		System.out.println(predictions.getPrimarys());
		
		// isolate predictions for primary interactions
		HashMap<Integer, Float> enactable = predictions.getEnactable(certitude_treshold);

		// find the most uncertain prediction
		float min_abs = 1f;
		int min_index = -1;
		for (int key: enactable.keySet()) {
			if (Math.abs(enactable.get(key)) < min_abs) {
				min_abs = Math.abs(enactable.get(key));
				min_index = key;
			}
		}
		
		if (min_abs < certitude_treshold) {
			// if uncertain primary action then explore it
			choice = Action.values()[predictions.translate(min_index)[0]];
		} else {
			choice = mostUseful(predictions);
		} 
		
		return choice;
	}

	private Action mostUseful(Data predictions) {
		float max_utility = Float.NEGATIVE_INFINITY;
		int max_index = Action.values().length - 1;

		List<Float> to_consider = predictions.getPrimarys();
		
		for (int i=0; i<to_consider.size(); i++) {
			if (to_consider.get(i) > 0) {
				if (utilities.get(Action.values()[i]) > max_utility) {
					max_utility = utilities.get(Action.values()[i]);
					max_index = i;
				}	
			}
		}
		
		return Action.values()[max_index];
	}

	@Override
	public void learn(Vector<Float> results) {
		perceptron.learn(results);
	}
	
	public Perceptron getPerceptron() {
		return perceptron;
	}

}

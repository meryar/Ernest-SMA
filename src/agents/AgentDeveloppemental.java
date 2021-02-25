package agents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
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
	public Action decide(Vector<Float> resultsTMinus1) {
		
		return decideAction(resultsTMinus1);
	}

	private Action decideAction(Vector<Float> resultsTMinus1) {
		Action choice;
		
		// get predictions of success from perceptron
		Vector<Float> predictions = perceptron.compute(resultsTMinus1);
		
		// isolate predictions for primary interactions
		List<Float> primary = predictions.subList(predictions.size() - Action.values().length, predictions.size());
		//System.out.println(primary);
		// find the most uncertain prediction
		float min_abs = 1f;
		float min = 1f;
		for (float pred: primary) {
			if (Math.abs(pred) < min_abs) {
				min_abs = Math.abs(pred);
				min = pred;
			}
		}
		
		if (min_abs < certitude_treshold) {
			// if uncertain primary action then explore it
			//System.out.println("learning primary actions!");
			int min_index = primary.indexOf(min);
			choice = Action.values()[min_index];
		} else {
			// if no uncertain primary then explore secondary actions
			
			// find enactable primaries
			Vector<Integer> enactable = new Vector<Integer>();
			for (int i=0; i<primary.size(); i++) {
				if (primary.get(i) > 0) {enactable.add(i);}
			}
			
			List<Float> secondary = predictions.subList(0, predictions.size() - Action.values().length);
			
			// find the most uncertain prediction
			int offset = (int) Main.colors.length * (int) Math.pow(2*Main.robot_vision_range + 1, 2);
			min_abs = 1f;
			min = 1f;
			int min_index = -1;
			for (int act: enactable) {
				for (int i=0; i<offset; i++) {
					float pred = secondary.get(act*offset + i);
					if (Math.abs(pred) < min_abs) {
						min_abs = Math.abs(pred);
						min = pred;
						min_index = act*offset + i;
					}
				}
			}
			
			if (min_abs < certitude_treshold) {
				// if uncertain secondary action then explore it
				//System.out.println("learning secondary actions " + min_index + " of certitude " + min);
				choice = Action.values()[(int) Math.floor(min_index / offset)];
			} else {
				//System.out.println("exploitation");
				choice = mostUseful(predictions);
			}
		} 
		
		return choice;
	}

	private Action mostUseful(Vector<Float> predictions) {
		float max_utility = Float.NEGATIVE_INFINITY;
		int max_index = Action.values().length - 1;

		List<Float> to_consider = predictions.subList(Action.values().length * Main.colors.length * (int) Math.pow(2*Main.robot_vision_range + 1, 2), predictions.size());
		//System.out.println(to_consider.size());
		
		for (int i=0; i<to_consider.size(); i++) {
			if (to_consider.get(i) > 0) {
				//System.out.println(utilities.get(Action.values()[i]) + " ? " + max_utility);
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

}

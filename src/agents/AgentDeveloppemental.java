package agents;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import agent_developpemental.Neuron;
import main.Main;
import robot.Action;

public class AgentDeveloppemental extends Agent{
	
	private static final float certitude_treshold = 0.6f;
	
	private Map<Action,Float> utilities;
	private Vector<Neuron> primaries, secondaries;
	private Vector<Float> lastPerception, lastPrediction;

	public AgentDeveloppemental(int data_size, int nb_interactions) {

		primaries = new Vector<Neuron>(nb_interactions);
		secondaries = new Vector<Neuron>(data_size - nb_interactions);
		
		for (int i=0; i<nb_interactions; i++) {
			primaries.add(new Neuron(data_size, Main.learning_rate));
		}
		for (int i=0; i<data_size - nb_interactions; i++) {
			secondaries.add(new Neuron(data_size, Main.learning_rate));
		}
		
		lastPrediction = new Vector<Float>();
		
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

	private Action decideAction(Vector<Float> perception) {
		lastPerception = perception;
		
		Action choice;
		
		// get predictions of success
		Vector<Float> predPrim = new Vector<Float>(primaries.size());
		Vector<Float> predSec = new Vector<Float>(secondaries.size());
		for (Neuron neuron: primaries) {
			predPrim.add(neuron.compute(perception));
		}
		for (Neuron neuron: secondaries) {
			predSec.add(neuron.compute(perception));
		}
		
		// isolate predictions for primary interactions
		HashMap<Integer, Float> enactable = new HashMap<Integer, Float>();
		for (int index=0; index<predPrim.size(); index++) {			
			enactable.put(index + secondaries.size(), predPrim.get(index));
		}
		for (int index=0; index<predSec.size(); index++) {			
			if (predPrim.get((int) (Math.floor(index / (secondaries.size() / Action.values().length)))) >= certitude_treshold) {
				enactable.put(index, predSec.get(index));
			}
		}
		
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
			if (min_index < secondaries.size()) {
				choice = Action.values()[(int)(min_index / (secondaries.size() / Action.values().length))];
			} else {
				choice = Action.values()[min_index - secondaries.size()];
			}
			
		} else {
			choice = mostUseful(predPrim);
		} 
		
		predSec.addAll(predPrim);
		lastPrediction = predSec;
		
		return choice;
	}

	private Action mostUseful(Vector<Float> predPrimaries) {
		float max_utility = Float.NEGATIVE_INFINITY;
		int max_index = Action.values().length - 1;
		
		for (int i=0; i<predPrimaries.size(); i++) {
			if (predPrimaries.get(i) > 0) {
				if (utilities.get(Action.values()[i]) > max_utility) {
					max_utility = utilities.get(Action.values()[i]);
					max_index = i;
				}	
			}
		}
		
		return Action.values()[max_index];
	}

	@Override
	public void learn(Vector<Float> trainingWeights) {
		for (int i=0; i<trainingWeights.size(); i++) {
			if (trainingWeights.get(i) != 0) {
				float error = trainingWeights.get(i) - lastPrediction.get(i);
				if (i < secondaries.size()) {
					secondaries.get(i).learn(lastPerception, Main.learning_rate * trainingWeights.get(i) * error);
				} else {
					primaries.get(i - secondaries.size()).learn(lastPerception, Main.learning_rate * trainingWeights.get(i) * error);
				}
			}
		}
	}
	

	public Vector<Float> getLastPerception() {
		return lastPerception;
	}

	public Vector<Float> getLastPrediction() {
		return lastPrediction;
	}
	
	public Vector<Neuron> getPrimaries(){
		return primaries;
	}
	
	public Vector<Neuron> getSecondaries(){
		return secondaries;
	}

}

package agents;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import agent_developpemental.Neuron;
import main.Main;
import robot.Action;

public class AgentDeveloppemental extends Agent{

	private static final float certitude_treshold = 0.6f;
	private static final int Thread_nb = 24;
	
	private Map<Action,Float> utilities;
	private Neuron[] primaries, secondaries;
	private float[] lastPrediction, lastPerception;
	
	public AgentDeveloppemental(int data_size, int nb_interactions) {

		primaries = new Neuron[nb_interactions];
		secondaries = new  Neuron[data_size - nb_interactions];
		
		for (int i=0; i<nb_interactions; i++) {
			primaries[i] = new Neuron(data_size, Main.learning_rate);
		}
		for (int i=0; i<data_size - nb_interactions; i++) {
			secondaries[i] = new Neuron(data_size, Main.learning_rate);
		}
		
		lastPrediction = new float[data_size];
		
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
	public Action decide(float[] resultsTMinus1) {
		
		return decideAction(resultsTMinus1);
	}

	private Action decideAction(float[] perception) {
		//long start = System.currentTimeMillis();
		lastPerception = perception;
		
		Action choice;
		float[] predSec = new float[secondaries.length];
		
		// get predictions of success.... but EVEN FASTER
		ExecutorService ex = Executors.newFixedThreadPool(Thread_nb);
	    int block_size = predSec.length/Thread_nb;
	    for (int i = 0; i < Thread_nb-1; i++) {
	        final int istart = i * block_size;
	        final int iend = (i + 1) * block_size;
	        ex.execute(new Runnable() {
				@Override
				public void run() {
					for (int j=istart; j<iend; j++) {
						predSec[j] = secondaries[j].compute(perception);
					}
				}
	        });
	    }
	    ex.execute(new Runnable() {
			@Override
			public void run() {
				for (int j=(Thread_nb-1)*block_size; j<secondaries.length; j++) {
					predSec[j] = secondaries[j].compute(perception);
				}
			}
        });
	    
	    ex.shutdown();
	    try {
			ex.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// get predictions of success
		Vector<Float> predPrim = new Vector<Float>(primaries.length);
		for (Neuron neuron: primaries) {
			predPrim.add(neuron.compute(perception));
		}/*
		for (Neuron neuron: secondaries) {
			predSec.add(neuron.compute(perception));
		}*/
		//System.out.println(predPrim);
		
		// isolate predictions for primary interactions
		HashMap<Integer, Float> enactable = new HashMap<Integer, Float>();
		for (int index=0; index<predPrim.size(); index++) {			
			enactable.put(index + secondaries.length, predPrim.get(index));
		}
		for (int index=0; index<predSec.length; index++) {			
			if (predPrim.get((int) (Math.floor(index / (secondaries.length / Action.values().length)))) >= certitude_treshold) {
				enactable.put(index, predSec[index]);
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
			// if uncertain interaction then explore it
			System.out.println("exploring interaction " + min_index + " of absolute certitude " + min_abs);
			if (min_index < secondaries.length) {
				choice = Action.values()[(int)(min_index / (secondaries.length / Action.values().length))];
			} else {
				choice = Action.values()[min_index - secondaries.length];
			}
			
		} else {
			System.out.println("agent in exploitation mode");
			choice = mostUseful(predPrim);
		} 
		
		float[] buffer = new float[predSec.length + predPrim.size()];
		for (int i=0; i<predSec.length; i++) {
			buffer[i] = predSec[i];
		}
		for (int i=0; i<predPrim.size(); i++) {
			buffer[i + predSec.length] = predPrim.get(i);
		}
		lastPrediction = buffer;
		//System.out.println("deciding took " + (System.currentTimeMillis() - start) + "ms");
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
	public void learn(float[] trainingWeights) {
		
		//long start = System.currentTimeMillis();
		ExecutorService ex = Executors.newFixedThreadPool(Thread_nb);
	    int block_size = trainingWeights.length/Thread_nb;
	    for (int i = 0; i < Thread_nb-1; i++) {
	        final int istart = i * block_size;
	        final int iend = (i + 1) * block_size;
	        ex.execute(new Runnable() {
				@Override
				public void run() {
					for(int next_id=istart; next_id<iend; next_id++) {
						if (trainingWeights[next_id] != 0) {
							float error = trainingWeights[next_id] - lastPrediction[next_id];
							if (Math.abs(error) > 0.02) {
								secondaries[next_id].learn(lastPerception, error);
							}
						}
					}
				}
	        });
	    }
	    ex.execute(new Runnable() {
			@Override
			public void run() {
				for(int next_id=(Thread_nb-1)*block_size; next_id<trainingWeights.length; next_id++) {
					if (trainingWeights[next_id] != 0) {
						float error = trainingWeights[next_id] - lastPrediction[next_id];
						if (Math.abs(error) > 0.02) {
							if (next_id < secondaries.length) {
								//System.out.println("learning secondary interaction " + next_id + " with error " + error);
								secondaries[next_id].learn(lastPerception, error);
							} else {
								//System.out.println("learning primary interaction " + next_id + " with error " + error);
								primaries[next_id - secondaries.length].learn(lastPerception, error);
							}
						}
					}
				}
			}
        });
	    ex.shutdown();
	    try {
			ex.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    //System.out.println("learning took " + (System.currentTimeMillis() - start) + "ms");
		/*
		for(int next_id=0; next_id<trainingWeights.size(); next_id++) {
			if (trainingWeights.get(next_id) != 0) {
				float error = trainingWeights.get(next_id) - lastPrediction.get(next_id);
				if (Math.abs(error) > 0.02) {
					if (next_id < secondaries.size()) {
						//System.out.println("learning secondary interaction " + next_id + " with error " + error);
						secondaries.get(next_id).learn(lastPerception, Main.learning_rate * trainingWeights.get(next_id) * error);
					} else {
						//System.out.println("learning primary interaction " + next_id + " with error " + error);
						primaries.get(next_id - secondaries.size()).learn(lastPerception, error);
					}
				}
			}
		}*/
	}
	

	public float[] getLastPerception() {
		return lastPerception;
	}

	public float[] getLastPrediction() {
		return lastPrediction;
	}
	
	public Neuron[] getPrimaries(){
		return primaries;
	}
	
	public Neuron[] getSecondaries(){
		return secondaries;
	}
	
}

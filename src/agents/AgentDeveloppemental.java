package agents;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import agent_developpemental.FastNeuron;
import main.Main;
import robot.Action;

public class AgentDeveloppemental extends Agent{

	private class DataStruct{
		ArrayList<Action> action;
		float certitude;
		int utility;
		int depth;
		int interaction;
		
		DataStruct(Action action_, float certitude_, int utility_, int depth_, int interaction_){
			action = new ArrayList<Action>();
			action.add(action_);
			certitude = certitude_;
			utility = utility_;
			depth = depth_;
			interaction = interaction_;
		}
		
		@SuppressWarnings("unchecked")
		DataStruct(DataStruct to_copy){
			action = (ArrayList<Action>) to_copy.action.clone();
			certitude = to_copy.certitude;
			utility = to_copy.utility;
			depth = to_copy.depth;
			interaction = to_copy.interaction;
		}
		
		void update(Action new_action, float certitude_, int utility_, int depth_, int interaction_) {
			action.add(new_action);
			certitude = certitude_;
			utility = utility_;
			depth = depth_;
			interaction = interaction_;
		}
	}
	
	private class Turn{
		Action intended, enacted;
		boolean motivated_by_curiosity;
		int target_interaction;
		ArrayList<Action> path;
		int reward;
		
		Turn(Action act_, boolean motivated_by_curiosity_, int target_interaction_, ArrayList<Action> path_){
			intended = act_;
			motivated_by_curiosity = motivated_by_curiosity_;
			target_interaction = target_interaction_;
			path = path_;
			reward = 0;
		}
		
		void setReward(int reward_, Action enacted_) {
			reward = reward_;
			enacted = enacted_;
		}
	}
	
	private static final float certitude_treshold = 0.6f;
	private static final int Thread_nb = 24;
	private static final int Research_depth = 4;
	private static final int history_size = 1000;

	private Map<Action,Integer> utilities;
	private FastNeuron[] primaries, secondaries;
	private float[] lastPrediction, lastPerception;
	private int data_size, id, target;
	private ArrayList<Action> currentPath;
	private ArrayList<Turn> history;
	private Action actionToEnact;
	
	

	public AgentDeveloppemental(int id_, int data_size_) {
		data_size = data_size_;
		id = id_;
		target = -1;
		actionToEnact = null;
		
		currentPath = new ArrayList<Action>();
		lastPrediction = new float[data_size];

		utilities = new HashMap<>();
		utilities.put(Action.MOVE_FWD, 5);
		utilities.put(Action.BUMP, -10);
		utilities.put(Action.FIGHT, -20);
		utilities.put(Action.EAT, 50);
		utilities.put(Action.FEAST, 200);
		utilities.put(Action.ROTATE_LEFT, -3);
		utilities.put(Action.ROTATE_RIGHT, -3);
		
		history = new ArrayList<Turn>(history_size);
	}

	public AgentDeveloppemental(int id, int data_size_, int nb_interactions) {
		this(id, data_size_);

		primaries = new FastNeuron[nb_interactions];
		secondaries = new FastNeuron[data_size - nb_interactions];

		for (int i=0; i<nb_interactions; i++) {
			primaries[i] = new FastNeuron(data_size, Main.learning_rate);
		}
		for (int i=0; i<data_size - nb_interactions; i++) {
			secondaries[i] = new FastNeuron(data_size, Main.learning_rate);
		}
	}

	public AgentDeveloppemental(int id, int data_size_, int nb_interactions, String filename) {
		this(id, data_size_);

		primaries = new FastNeuron[nb_interactions];
		secondaries = new  FastNeuron[data_size - nb_interactions];

		load(filename);
	}

	@Override
	public Action decide(float[] resultsTMinus1) {
		actionToEnact = decideAction(resultsTMinus1);
		return actionToEnact;
	}

	private Action decideAction(float[] perception) {
		//long start = System.currentTimeMillis();
		lastPerception = perception;
		
		boolean curiosity_motivated = true;
		Action choice;

		// 1) get primary and secondary predictions
		Vector<Float> predPrim = new Vector<Float>(primaries.length);
		for (FastNeuron neuron: primaries) {
			predPrim.add(neuron.compute(perception));
		}
		float[] predSec = getPredSec(perception);
		//System.out.println(predPrim);
		
		// 1.1) storing predictions
		float[] buffer = new float[predSec.length + predPrim.size()];
		for (int i=0; i<predSec.length; i++) {
			buffer[i] = predSec[i];
		}
		for (int i=0; i<predPrim.size(); i++) {
			buffer[i + predSec.length] = predPrim.get(i);
		}
		lastPrediction = buffer;
		
		if (!currentPath.isEmpty()) {
			choice = currentPath.get(0);
			currentPath.remove(0);
			history.add(new Turn(choice, curiosity_motivated, target, (ArrayList<Action>) currentPath.clone()));
			return choice;
		}
		
		// 2) check if there is something interesting to explore
		
		// 2.1) isolate enactable interactions
		HashMap<Integer, Float> enactable = new HashMap<Integer, Float>();
		for (int index=0; index<predPrim.size(); index++) {			
			enactable.put(index + secondaries.length, predPrim.get(index));
		}
		for (int index=0; index<predSec.length; index++) {			
			if (predPrim.get((int) (Math.floor(index / (secondaries.length / Action.values().length)))) >= certitude_treshold 
					&& FastNeuron.isInteresting(secondaries[index], Math.signum(predSec[index]))) {
				enactable.put(index, predSec[index]);
			}
		}

		// 2.2) find the most uncertain prediction
		float min_abs = 1f;
		int min_index = -1;
		for (int key: enactable.keySet()) {
			if (Math.abs(enactable.get(key)) < min_abs) {
				min_abs = Math.abs(enactable.get(key));
				min_index = key;
			}
		}
		
		// 2.3) check if there is an uncertain prediction
		if (min_abs < certitude_treshold) {
			// 2.3.1) if uncertain interaction then explore it
			System.out.println("exploring interaction " + min_index + " of absolute certitude " + min_abs);
			target = min_index;
			if (min_index < secondaries.length) {
				choice = Action.values()[(int)(min_index / (secondaries.length / Action.values().length))];
			} else {
				choice = Action.values()[min_index - secondaries.length];
			}
		} else {
		// 3) else check if there is something interesting to explore after each enactable primary
			// 3.1) get the least certain or most valuable interaction on each possible path
			short nb_enctable_act = 0; 
			Vector<Action> enactable_act = new Vector<Action>(); 
			for (int i=0; i<predPrim.size(); i++) if (predPrim.get(i) >= certitude_treshold) {
				nb_enctable_act += 1;
				enactable_act.add(Action.values()[i]);
			}
			DataStruct[] choice_list = new DataStruct[nb_enctable_act];
			for (int i=0; i<nb_enctable_act; i++) {
				float[] next_context = propagate(perception, enactable_act.get(i), predSec);
				if (!next_context.equals(perception)) {
					DataStruct data = new DataStruct(enactable_act.get(i), 0, utilities.get(enactable_act.get(i)), 0, 0);
					choice_list[i] = getValue(data, 
						next_context);
				}
			}
			
			// 3.2) choose best action 
			DataStruct chosen = choose_function(choice_list); 
			currentPath = chosen.action;
			choice = currentPath.get(0);
			currentPath.remove(0);
			curiosity_motivated = chosen.certitude != 1;
			target = chosen.interaction;
		} 
		
		
		//System.out.println("deciding took " + (System.currentTimeMillis() - start) + "ms");
		
		// 4) storing decision for comportment evaluation
		history.add(new Turn(choice, curiosity_motivated, target, (ArrayList<Action>) currentPath.clone()));
		
		return choice;
	}

	private float[] propagate(float[] perception, Action action, float[] predSec) {
		float[] new_context = new float[perception.length];
		Arrays.fill(new_context, 0);
		
		int id_action = 0;
		for (int i=0; i<Action.values().length; i++) {
			if (Action.values()[i].equals(action)) {
				id_action = i;
				break;
			}
		}
		
		int action_offset = secondaries.length / Action.values().length;
		for (int i=0; i < action_offset; i++) {
			new_context[id_action*action_offset + i] = predSec[id_action*action_offset + i] > certitude_treshold? 1: 0;
		}
		new_context[predSec.length + id_action] = 1; 
		
		return new_context;
	}

	private DataStruct choose_function(DataStruct[] choice_list) {
		System.out.println("choosing best candidate amongst:");
		
		DataStruct best = null;
		boolean under_treshold = false;
		float certainty = 1;
		int utility = Integer.MIN_VALUE;
		int depth = Integer.MAX_VALUE;
		
		for(DataStruct data: choice_list) {
			System.out.println("action= " + data.action + " / certitude= " + data.certitude + " / utility= " + data.utility 
					+ " / depth= " + data.depth + " / interaction= " + data.interaction);
			if (under_treshold) {
				if (Math.abs(data.certitude) < certainty || (Math.abs(data.certitude) == certainty && data.depth < depth)) {
					certainty = Math.abs(data.certitude);
					best = data;
					depth = data.depth;
				}
			} else if (Math.abs(data.certitude) < certitude_treshold) {
				best = data;
				certainty = Math.abs(data.certitude);
				depth = data.depth;
				under_treshold = true;
			} else if (data.utility > utility) {
				best = data;
				utility = data.utility;
				depth = data.depth;
			}
		}
		System.out.println("winner: action=" + best.action + "/ certitude=" + best.certitude 
				+ "/ utility=" + best.utility + "/ depth=" + best.depth + "/ interaction=" + best.interaction);
		return best;
	}

	private DataStruct getValue(DataStruct data_, float[] context) {
		DataStruct data = new DataStruct(data_);
		System.out.println("exploring on path " + data.action.toString() + " at depth " + data.depth);
		
		Vector<Action> enactables = new Vector<Action>();
		
		// 1) checking if uncertain interesting prediction
		// 1.1) making primary and secondary predictions
		Vector<Float> predPrim = new Vector<Float>(primaries.length);
		for (FastNeuron neuron: primaries) {
			predPrim.add(neuron.compute(context));
		}
		float[] predSec = getPredSec(context);
		
		// 1.2) isolate enactable interactions
		HashMap<Integer, Float> enactable = new HashMap<Integer, Float>();
		for (int index=0; index<predPrim.size(); index++) {			
			enactable.put(index + secondaries.length, predPrim.get(index));
			if (predPrim.get(index) >= certitude_treshold) enactables.add(Action.values()[index]);
		}
		/*for (int index=0; index<predSec.length; index++) {			
			if (predPrim.get((int) (Math.floor(index / (secondaries.length / Action.values().length)))) >= certitude_treshold 
					&& FastNeuron.isInteresting(secondaries[index], Math.signum(predSec[index]))) {
				enactable.put(index, predSec[index]);
			}
		}*/
		
		DataStruct[] dataset = new DataStruct[enactables.size()];

		// 1.3) find the most uncertain prediction
		float min_abs = 1;
		float min = 0;
		int min_key = -1;
		for (int key: enactable.keySet()) {
			if (Math.abs(enactable.get(key)) < min_abs) {
				min_abs = Math.abs(enactable.get(key));
				min = enactable.get(key);
				min_key = key;
			}
		}
		
		// 1.4) if under treshold, return it.
		if (min_abs < certitude_treshold) {
			System.out.println("found uncertain prediction " + min_key + " at depth " + data.depth + " of certitude " + min);
			Action act;
			if (min_key < secondaries.length) {
				act = Action.values()[(int) (Math.floor(min_key / (secondaries.length / Action.values().length)))];
			} else {
				act = Action.values()[min_key - secondaries.length];
			}
			data.update(act, min, data.utility + utilities.get(act), data.depth+1, min_key);
			return data;
		}
		// 2) else if not at max depth, look further
		else if (data.depth < Research_depth) {
			System.out.println("going deeper because min absolute certitude is " + min_abs);
			for (int i=0; i<enactables.size(); i++) {
				float[] next_context = propagate(context, enactables.get(i), predSec);
				if (!next_context.equals(context)) {
					DataStruct data1 = new DataStruct(data);
					data1.update(enactables.get(i), 0, data1.utility + utilities.get(enactables.get(i)), data1.depth + 1, -1);
					dataset[i] = getValue(data1, propagate(context, enactables.get(i), predSec)); 
				}
			}
		}
		// 3) else return action with highest utility
		else {
			System.out.println("max depth(" + data.depth + ") reached, returning interaction with highest utility");
			for (int i=0; i<enactables.size(); i++) {
				DataStruct data1 = new DataStruct(data);
				data1.update(enactables.get(i), 1, data1.utility + utilities.get(enactables.get(i)), data1.depth + 1, -1);
				dataset[i] = data1;
			}
		}
		
		return choose_function(dataset);
	}

	private float[] getPredSec(float[] perception) {
		float[] res = new float[secondaries.length];
		
		// get predictions of success.... but EVEN FASTER
				ExecutorService ex = Executors.newFixedThreadPool(Thread_nb);
				int block_size = secondaries.length/Thread_nb;
				for (int i = 0; i < Thread_nb-1; i++) {
					final int istart = i * block_size;
					final int iend = (i + 1) * block_size;
					ex.execute(new Runnable() {
						@Override
						public void run() {
							for (int j=istart; j<iend; j++) {
								res[j] = secondaries[j].compute(perception);
							}
						}
					});
				}
				ex.execute(new Runnable() {
					@Override
					public void run() {
						for (int j=(Thread_nb-1)*block_size; j<secondaries.length; j++) {
							res[j] = secondaries[j].compute(perception);
						}
					}
				});

				ex.shutdown();
				try {
					ex.awaitTermination(1, TimeUnit.MINUTES);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		
		return res;
	}

	@SuppressWarnings("unused")
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

		if (history.size() >= history_size) save_history();

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
							secondaries[next_id].succeded(trainingWeights[next_id] == 1);
							float error = trainingWeights[next_id] - lastPrediction[next_id];
							if (Math.abs(error) > 0.01) {
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
						if (Math.abs(error) > 0.01) {
							if (next_id < secondaries.length) {
								//System.out.println("learning secondary interaction " + next_id + " with error " + error);
								secondaries[next_id].succeded(trainingWeights[next_id] == 1);
								secondaries[next_id].learn(lastPerception, error);
							} else {
								//System.out.println("learning primary interaction " + next_id + " with error " + error);
								primaries[next_id - secondaries.length].succeded(trainingWeights[next_id] == 1);
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
	}


	public float[] getLastPerception() {
		return lastPerception;
	}

	public float[] getLastPrediction() {
		return lastPrediction;
	}

	public FastNeuron[] getPrimaries(){
		return primaries;
	}

	public FastNeuron[] getSecondaries(){
		return secondaries;
	}

	public void load(String file_name) {
		try {
			File myObj = new File(file_name);
			Scanner myReader = new Scanner(myObj);
			int counter = 0;
			while (myReader.hasNextLine()) {
				if (counter < data_size - Action.values().length) {
					secondaries[counter] = new FastNeuron(data_size, Main.learning_rate, myReader.nextLine());
				} else if(counter < data_size){
					primaries[counter + Action.values().length - data_size] = new FastNeuron(data_size, Main.learning_rate, myReader.nextLine());
				} else {
					System.err.println("Too many lines in file: " + file_name);
				}
				counter += 1;
				if(counter % 100 == 0) System.out.println("loaded " + counter + " weights");
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("no such file: " + file_name);
			e.printStackTrace();
		}
	}

	public void save(String file_name) {

		try {
			File myObj = new File(file_name);
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
				try {
					FileWriter myWriter = new FileWriter(file_name);

					int counter = 0;
					for(FastNeuron neuron: secondaries) {
						myWriter.write(neuron.getData());
						for (float weight: neuron.getWeights()) {
							myWriter.write(weight + " ");
						}
						myWriter.write("\n");
						System.out.println("neuron " + counter + " parsed");
						counter++;
					}
					for(FastNeuron neuron: primaries) {
						myWriter.write(neuron.getData());
						for (float weight: neuron.getWeights()) {
							myWriter.write(weight + " ");
						}
						myWriter.write("\n");
					}

					myWriter.close();
					System.out.println("Successfully wrote to the file.");
				} catch (IOException e) {
					System.out.println("An error occurred.");
					e.printStackTrace();
				}
			} else {
				System.out.println("File already exists.");
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	
	/*
	public void updateNeurons() {
		for (SelectiveNeuron n: primaries) n.update();
		for (SelectiveNeuron n: secondaries) n.update();
	}*/
	
	public int correspondingInteraction(int index) {
		if (index < data_size - Action.values().length) {
			return (int) Math.floor(index / (data_size - Action.values().length)/Action.values().length) ;
		} else {
			return (int) (index - (data_size - Action.values().length));
		}
	}
	
	
	private void save_history() {
		String file_name = "trace_agent_" + id + ".txt";
		try {
			File myObj = new File(file_name);
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("adding to trace.");
			}
			
			FileWriter myWriter = new FileWriter(file_name, true);
			
			for (Turn turn: history) {
				myWriter.write(turn.intended + ";" 
						+ turn.target_interaction + ";" 
						+ turn.path.toString() + ";" 
						+ turn.motivated_by_curiosity + ";"
						+ turn.enacted+ ";"
						+ turn.reward + "\n");	
			}
				
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
			
			history.clear();
			
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	@Override
	public void storeResult(Action lastEnacted) {
		if (lastEnacted != actionToEnact) currentPath.clear();
		if (!history.isEmpty()) {
			history.get(history.size()-1).setReward(utilities.get(lastEnacted), lastEnacted);
		}
	}

}

package bundle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import agent_developpemental.FastNeuron;
import agents.AgentDeveloppemental;
import environment.Direction;
import main.Main;
import robot.Action;

public class Bundle {
	
	int entry;
	ArrayList<FastNeuron> content; 
	ArrayList<Integer> index; 
	
	public class Pair<T,U>{
		public T member1;
		public U member2;
		
		public Pair(T m1, U m2){
			member1 = m1;
			member2 = m2;
		}

		public T left() { return member1;}
		public U right() { return member2;}
	}
	
	public Bundle(int entry_) {
		entry = entry_;
		content = new ArrayList<FastNeuron>();
		index = new ArrayList<Integer>();
	}
	
	public void addNeuron(FastNeuron neuron, int neuronId) {
		content.add(neuron);
		index.add(neuronId);
	}
	
	public void addNeurons(ArrayList<FastNeuron> neuron, ArrayList<Integer> ids) {
		content.addAll(neuron);
		index.addAll(ids);
	}
	
	public float total_certitude(float[] entry_) {
		float res = 0;
		for (int i=0; i<content.size(); i++) {
			res += content.get(i).compute(entry_);
		}
		
		return res;
	}
	
	public static void main(String[] args){
		HashMap<Pair<Integer, Action>, Bundle> bundles = new HashMap<Pair<Integer, Action>, Bundle>();
		
		String file_name = "ressources/agents/" + "agent_0" + ".txt";
		int nb_sensors = (int) Math.pow(Main.robot_vision_range*2 +1, 2);
		int input_size = ((Main.colors.length + Direction.values().length - 1) * (nb_sensors) + 1) * Action.values().length;
		
		AgentDeveloppemental agent = new AgentDeveloppemental(0, input_size, Action.values().length, file_name);
		
		
		long start = System.currentTimeMillis();
		
		
		
		for (int i=0; i<agent.getSecondaries().length; i++) {
			// create entry
			float[] entry = new float[input_size];
			entry[i] = 1;
			entry[agent.getSecondaries().length + (int)(Math.floor(i / ((Main.colors.length + Direction.values().length - 1) * (nb_sensors))))] = 1;
			float[] max = new float[Action.values().length];
			for (int k=0; k<Action.values().length; k++) max[k] = 0;
			
			// calculate results
			Vector<Float> predPrim = new Vector<Float>(agent.getPrimaries().length);
			for (int j=0; j<agent.getPrimaries().length; j++) {
				float res = agent.getPrimaries()[j].compute(entry);
				max[j] = Math.max(max[j], res);
				predPrim.add(res);
			}
			float[] predSec = agent.getPredSec(entry);
			for (int j=0; j<agent.getSecondaries().length; j++) {
				float res = agent.getSecondaries()[j].compute(entry);
				int index = (int)(Math.floor(j / ((Main.colors.length + Direction.values().length - 1) * (nb_sensors))));
				max[index] = Math.max(max[index], res);
				predSec[j] = res;
			}
			

			for (int k=0; k<Action.values().length; k++) {
				if (max[k] > 0.2 && max[k] < 0.6){
					ArrayList<FastNeuron> winners = new ArrayList<FastNeuron>();
					ArrayList<Integer> winnersIds = new ArrayList<Integer>();
					if (predPrim.get(k) >= 0.5*max[k]) {
						winners.add(agent.getPrimaries()[k]);
						winnersIds.add(k + predSec.length);
					}
					for(int j=0; j<predSec.length/Action.values().length; j++) {
						if (predSec[k*(predSec.length/Action.values().length) + j] >= 0.5*max[k]) {
							winners.add(agent.getSecondaries()[k*(predSec.length/Action.values().length) + j]);
							winnersIds.add(k*(predSec.length/Action.values().length) + j);
						}
					}
					
					if (winners.size() > 1) {
						Bundle b = new Bundle(i);
						b.addNeurons(winners, winnersIds);
						bundles.put(b.new Pair<Integer, Action>(i,Action.values()[k]), b);
					}
				}
			}
		}
		System.out.println("calculs duration = " + (System.currentTimeMillis() - start) + "ms");
		System.out.println(bundles.size() + " bundles for " + input_size + " entries");
		HashMap<Integer, Integer> counter = new HashMap<Integer, Integer>();
		for (Pair<Integer, Action> id: bundles.keySet()) {
			int nb = bundles.get(id).content.size();
			if (counter.containsKey(nb)) {
				counter.put(nb, counter.get(nb)+1);
			} else counter.put(nb, 1);
		}
		
		for (int nb: counter.keySet()) {
			System.out.println(counter.get(nb) + " bundles of " + nb + " neurons");
		}
		
		for (Pair<Integer, Action> id: bundles.keySet()) {
			System.out.print("bundle " + id.left() + "/" + id.right() + " contains neurons: ");
			for (int nId: bundles.get(id).index) {
				System.out.print(nId + " / ");
			}
			System.out.println("");
		}
	}
}

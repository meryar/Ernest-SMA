package agent_developpemental;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import robot.Action;

public class SelectiveNeuron {

	
	private static float error_margin = 0.25f;
	
	private int entries_number;
	private long nb_fails, nb_successes;
	private float avg_err_suc, avg_err_fail;
	private float[] weights;
	private float alpha; 				// learning rate
	private Set<Short> map;
	private float relevant_weight_treshold;
	
	public float[] max_abs_weights;
	

	public SelectiveNeuron(int input_size, float learning_rate, float treshold) {
		entries_number = input_size;
		alpha = learning_rate;
		avg_err_fail = 0;
		avg_err_suc = 0;
		nb_fails = 0;
		nb_successes = 0;
		relevant_weight_treshold = treshold;

		weights = new float[entries_number];
		Arrays.fill(weights, 0);

		max_abs_weights = new float[Action.values().length];
		map = new HashSet<Short>();
	}
	
	public SelectiveNeuron(int input_size, float learning_rate, float treshold, String line) {
		entries_number = input_size;
		weights = new float[entries_number];
		alpha = learning_rate;
		max_abs_weights = new float[Action.values().length];
		avg_err_fail = 0;
		avg_err_suc = 0;
		nb_fails = 0;
		nb_successes = 0;
		
		String[] tab = line.split(" ");
		for (int i=0; i<tab.length; i++) {
			weights[i] = Float.parseFloat(tab[i]);
		}
		max_abs_weights = new float[Action.values().length];
		map = new HashSet<Short>();
	}

	public float compute(float[] perception) {
		if (perception.length != entries_number) {
			System.out.println("cumputing error-> entry size: " + perception.length + " / expected: " + entries_number);
		}
		
		float sum = 0;
		if (!map.isEmpty()) {
			for(int key: map) sum += weights[key] * perception[key];
		} else {
			for (int i=0; i<entries_number; i++) sum += weights[i] * perception[i];
		}
		
		return (float) (1 / (1+Math.exp(-sum)))*2-1;
	}

	public void learn(float[] lastPerception, float error) {
		float alpha_prime;
		float rate;
		
		for (int i = 0; i<max_abs_weights.length; i++) {
			max_abs_weights[i] = 0;
		}
		
		if (error > 0) {
			rate = (float)nb_successes / (nb_fails + nb_successes);
			avg_err_fail = (error + avg_err_fail * 100) / 101;
			alpha_prime = alpha / (rate * 2); 
		} else {
			rate = (float)nb_fails / (nb_fails + nb_successes);
			avg_err_suc = (error + avg_err_suc * 100) / 101;
			alpha_prime = alpha / (rate * 2);
		}
		
		alpha_prime = Math.max(alpha_prime, alpha);
		alpha_prime = Math.min(alpha_prime, 1);
		
        for (int i=0; i<weights.length; i++) {
        	float delta = alpha_prime * error * lastPerception[i];
            weights[i] = weights[i] + delta;
            
            max_abs_weights[correspondingInteraction(i)] = Math.max(Math.abs(weights[i]), max_abs_weights[correspondingInteraction(i)]);  
	        
        }
	}

	public int correspondingInteraction(int index) {
		if (index < entries_number - Action.values().length) {
			return (int) Math.floor(index / (entries_number - Action.values().length)/Action.values().length) ;
		} else {
			return (int) (index - (entries_number - Action.values().length));
		}
	}

	public void update() {
		map.clear();
		float max = 0;
		for (float weight: weights) { if (Math.abs(weight) > max) max = weight; }
		
		for (short i=0; i<weights.length; i++) {
			if (Math.abs(weights[i]) >= max*relevant_weight_treshold) map.add(i);
		}
	}
	
	public static boolean isInteresting(SelectiveNeuron neuron, float sign) {
		if (sign == -1) {
			return neuron.avg_err_fail <= error_margin;
		} else if(sign == 1) {
			return -neuron.avg_err_suc <= error_margin;
		} else {
			return neuron.avg_err_fail <= error_margin || -neuron.avg_err_suc <= error_margin;
		}
	}
	
	public void succeded(boolean succeded) {
		if (succeded) {
			nb_successes++;
		} else {
			nb_fails++;
		}
	}
	
	public void printRatio() {
		System.out.println("successes: " + nb_successes + " / failures: " + nb_fails + " / ratio: " + (float)nb_successes / (nb_fails + nb_successes));
	}
	
	public float[] getWeights() {
		return weights;
	}
	
}

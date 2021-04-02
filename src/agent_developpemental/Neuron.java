package agent_developpemental;

import java.util.Vector;

import robot.Action;

public class Neuron {
	
	private static float error_margin = 0.25f;
	
	private int entries_number;
	private long nb_fails, nb_successes;
	private Vector<Float> weights;
	private float alpha; 				// learning rate
	private float avg_err_suc, avg_err_fail;
	
	public float[] max_abs_weights;
	
	public Neuron(int input_size, float learning_rate) {
		weights = new Vector<Float>();
		entries_number = input_size;
		alpha = learning_rate;
		max_abs_weights = new float[Action.values().length];
		avg_err_fail = 0;
		avg_err_suc = 0;
		nb_fails = 0;
		nb_successes = 0;
		
		for (int i=0; i<entries_number; i++) {
			weights.add(0f);
		}
	}

	public Neuron(int input_size, float learning_rate, String line) {
		weights = new Vector<Float>();
		entries_number = input_size;
		alpha = learning_rate;
		max_abs_weights = new float[Action.values().length];
		avg_err_fail = 0;
		avg_err_suc = 0;
		nb_fails = 0;
		nb_successes = 0;
		
		for (String nb: line.split(" ")) {
			weights.add(Float.parseFloat(nb));
		}
	}

	public Float compute(float[] perception) {
		if (perception.length != weights.size()) {
			System.out.println("entry size: " + perception.length + " / weigths number: " + weights.size());
		}
		
		Float sum = (float) 0;
		for (int i=0; i<entries_number; i++) {
			sum += weights.get(i) * perception[i];
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
		
        for (int i=0; i<weights.size(); i++) {
        	float delta = alpha_prime * error * lastPerception[i];
            weights.set(i, weights.get(i) + delta);
            
            max_abs_weights[correspondingInteraction(i)] = Math.max(Math.abs(weights.get(i)), max_abs_weights[correspondingInteraction(i)]);  
	        
        }
	}
	
	public int correspondingInteraction(int index) {
		if (index < weights.size() - Action.values().length) {
			return (int) Math.floor(index / (weights.size() - Action.values().length)/Action.values().length) ;
		} else {
			return (int) (index - (weights.size() - Action.values().length));
		}
	}


	public Vector<Float> getWeights() {
		return weights;
	}
	
	public static boolean isInteresting(Neuron neuron, float sign) {
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
}

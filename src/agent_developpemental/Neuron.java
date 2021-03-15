package agent_developpemental;

import java.util.Vector;

import robot.Action;

public class Neuron {
	
	private int entries_number;
	private Vector<Float> weights;
	private float c; 				// learning rate
	private float learn_sum;
	
	public float[] max_abs_weights;
	
	public Neuron(int input_size, float learning_rate) {
		weights = new Vector<Float>();
		entries_number = input_size;
		c = learning_rate;
		max_abs_weights = new float[Action.values().length];
		learn_sum = 0;
		
		for (int i=0; i<entries_number; i++) {
			weights.add(0f);
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
	
	public float learn(float[] lastPerception, float error) {
		for (int i = 0; i<max_abs_weights.length; i++) {
			max_abs_weights[i] = 0;
		}
		
		if (error * learn_sum < 0) {
			if (error > 0) {
				error = (float) Math.max(error, -0.05*learn_sum);
			}
			if (error < 0) {
				error = (float) Math.min(error, -0.05*learn_sum);
			}
		}
		
        for (int i=0; i<weights.size(); i++) {
        	
            weights.set(i, weights.get(i) + c * error * lastPerception[i]);
            
            if (i < weights.size() - 1) {
	            max_abs_weights[correspondingInteraction(i)] = Math.max(Math.abs(weights.get(i)), max_abs_weights[correspondingInteraction(i)]);  
	        }
        }
        learn_sum += error; 
        
        return max_abs_weights[0];
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
}

package agent_developpemental;

import java.util.Vector;

import robot.Action;

public class Neuron {
	
	private int entries_number;
	private Vector<Float> weights;
	private float c; 				// learning rate
	
	public float max_abs_weight, min_weight; 
	
	public Neuron(int input_size, float learning_rate) {
		weights = new Vector<Float>();
		entries_number = input_size;
		c = learning_rate;
		max_abs_weight = 1f;
		min_weight = -0.001f;
		
		for (int i=0; i<entries_number; i++) {
			weights.add(0f);
		}
	}

	public Float compute(Vector<Float> entry) {
		assert (entry.size() != entries_number): "illegal number of entries: " + entry.size() + " instead of " + entries_number;
		
		Float sum = (float) 0;
		for (int i=0; i<entries_number; i++) {
			sum += weights.get(i) * entry.get(i);
		}
		return (float) ( 1 / (1+Math.exp(-sum)))*2-1;
	}
	
	public float learn(Vector<Float> entry, float error) {
		min_weight = -0.001f;
        for (int i = 0; i < weights.size(); i++) {
            weights.set(i, weights.get(i) + c * error * entry.get(i));
            if (i < weights.size() - Action.values().length) {
	            max_abs_weight = Math.max(Math.abs(Math.abs(weights.get(i))), max_abs_weight);
	            min_weight = Math.min(weights.get(i), min_weight);
            }
        }
        return max_abs_weight;
	}


	public Vector<Float> getWeights() {
		return weights;
	}
}

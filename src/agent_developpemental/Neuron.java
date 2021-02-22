package agent_developpemental;

import java.util.Vector;

public class Neuron {
	
	private int entries_number;
	private Vector<Float> weights;
	private float c; 				// learning rate
	
	public Neuron(int input_size, float learning_rate) {
		weights = new Vector<Float>();
		entries_number = input_size;
		c = learning_rate;
		for (int i=0; i<entries_number; i++) {
			weights.add((float)(Math.random() * 2 - 1));
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
	
	public void learn(Vector<Float> entry, float error) {
        for (int i = 0; i < weights.size(); i++) {
            weights.set(i, weights.get(i) + c * error * entry.get(i));
        }
	}

}

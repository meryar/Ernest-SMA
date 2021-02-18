package agent_developpemental;

import java.util.Vector;

public class Neuron {
	
	private int entries_number;
	private Vector<Float> weights;
	
	public Neuron(int input_size) {
		weights = new Vector<Float>();
		entries_number = input_size;
		for (int i=0; i<input_size; i++) {
			weights.add((float)(Math.random() * 2 - 1));
		}
	}

	public Float compute(Vector<Float> entry) {
		if (entry.size() != entries_number) {
			throw new IllegalStateException("illegal number of entries: " + entry.size() + " instead of " + entries_number);
		}
		Float sum = (float) 0;
		for (int i=0; i<entries_number; i++) {
			sum += weights.get(i) * entry.get(i);
		}
		return (float) ( 1 / (1+Math.exp(-sum)))*2-1;
	}

}

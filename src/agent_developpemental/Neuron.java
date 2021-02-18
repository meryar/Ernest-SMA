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
		// TODO Auto-generated method stub
		return null;
	}

}

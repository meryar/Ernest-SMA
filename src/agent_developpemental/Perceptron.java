package agent_developpemental;

import java.util.Vector;

public class Perceptron {

	Vector<Neuron> neurons;
	float bias;
	
	public Perceptron() {
		neurons = new Vector<Neuron>();
		bias = (float) 1;
	}
	
	public Perceptron(int input_size, int output_size) {
		this();
		for (int i=0; i<output_size; i++) {
			neurons.add(new Neuron(input_size + 1));
		}
	}
	
	public Vector<Float> compute(Vector<Float> entry){
		Vector<Float> res = new Vector<Float>();
		
		for (Neuron neuron: neurons) {
			res.add(neuron.compute(entry));
		}
		
		return res;
	}

}

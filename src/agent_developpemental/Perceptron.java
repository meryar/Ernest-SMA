package agent_developpemental;

import java.util.Vector;

public class Perceptron {

	private static final float bias = 1f;
	
	private Vector<Neuron> neurons;
	private Vector<Float> lastPrediction;
	
	public Perceptron() {
		neurons = new Vector<Neuron>();
		lastPrediction = new Vector<Float>();
	}
	
	public Perceptron(int input_size, int output_size) {
		this();
		for (int i=0; i<output_size; i++) {
			neurons.add(new Neuron(input_size + 1));
		}
	}
	
	public Vector<Float> compute(Vector<Float> entry){
		Vector<Float> res = new Vector<Float>();
		entry.add(bias);
		lastPrediction.clear();
		
		for (Neuron neuron: neurons) {
			float certitude = neuron.compute(entry);
			lastPrediction.add(certitude);
			res.add(certitude);
		}
		
		return res;
	}

}

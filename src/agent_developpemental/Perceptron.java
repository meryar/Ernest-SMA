package agent_developpemental;

import java.util.Vector;

import main.Main;

public class Perceptron {

	private static final float bias = 1f;
	
	private Vector<Neuron> neurons;
	private Vector<Float> lastEntry;
	
	public Perceptron() {
		neurons = new Vector<Neuron>();
		lastEntry = new Vector<Float>();
	}
	
	public Perceptron(int input_size, int output_size) {
		this();
		for (int i=0; i<output_size; i++) {
			neurons.add(new Neuron(input_size + 1, Main.learning_rate));
		}
	}
	
	public Vector<Float> compute(Vector<Float> entry){
		Vector<Float> res = new Vector<Float>();
		entry.add(bias);
		lastEntry = (Vector<Float>) entry.clone();
		
		for (Neuron neuron: neurons) {
			float certitude = neuron.compute(entry);
			res.add(certitude);
		}
		
		return res;
	}

	public void learn(Vector<Float> trainingWeights) {
		assert (trainingWeights.size() == neurons.size()): "error: number of learning weights different from neurons number!";
		
		for (int i=0; i<neurons.size(); i++) {
			if (trainingWeights.get(i) != 0f) {
				neurons.get(i).learn(lastEntry, trainingWeights.get(i));
			}
		}
	}
}

package agent_developpemental;

import java.util.Vector;

import main.Main;

public class Perceptron {
	
	private Vector<Neuron> neurons;
	public float max_abs_weight;
	private Vector<Float> lastEntry;
	private Vector<Float> lastPrediction;
	
	public Perceptron() {
		neurons = new Vector<Neuron>();
		lastEntry = new Vector<Float>();
		lastPrediction = new Vector<Float>();
		max_abs_weight = 1;
	}
	
	public Perceptron(int input_size, int output_size) {
		this();
		for (int i=0; i<output_size; i++) {
			neurons.add(new Neuron(input_size, Main.learning_rate));
		}
		
		lastPrediction.setSize(output_size);
		lastPrediction.replaceAll(e -> 0f);
	}
	
	public Vector<Float> compute(Vector<Float> entry){
		Vector<Float> res = new Vector<Float>();
		lastEntry = (Vector<Float>) entry.clone();
		
		for (Neuron neuron: neurons) {
			float certitude = neuron.compute(entry);
			res.add(certitude);
		}
		lastPrediction = res;
		return res;
	}

	public void learn(Vector<Float> trainingWeights) {
		assert (trainingWeights.size() == neurons.size()): "error: number of learning weights different from neurons number!";
		
		for (int i=0; i<neurons.size(); i++) {
			if (trainingWeights.get(i) != 0) {
				float max = neurons.get(i).learn(lastEntry, trainingWeights.get(i) - lastPrediction.get(i));
				max_abs_weight = Math.max(max_abs_weight, max);
			}
		}
	}

	public Vector<Neuron> getNeurons() {
		return neurons;
	}
}

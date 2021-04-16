package agent_developpemental;

import java.util.HashMap;
import java.util.Set;

public class AdaptativeNeuron {

	private HashMap<Integer, Float> weights;
	private float alpha;
	
	public AdaptativeNeuron(float learning_rate) {
		weights = new HashMap<Integer, Float>();
		alpha = learning_rate;
	}
	
	public float compute(Set<Integer> perception) {
		
		float sum = 0;
		
		for(Integer interaction: perception) {
			if (weights.containsKey(interaction)) {
				sum += weights.get(interaction);
			}
		}
		
		return (float) (1 / (1+Math.exp(-sum)))*2-1;
	}
	
	public void learn(Set<Integer> lastPerception, float error) {
		
		for (int interaction: lastPerception) {
			if (weights.containsKey(interaction)) {
				weights.put(interaction, weights.get(interaction) + error * alpha);
			} else {
				weights.put(interaction, error*alpha);
			}
		}
		
	}
}

package agents;

import robot.Action;

public abstract class Agent {

	public Agent() {
	}

	public abstract Action decide(float[] resultsTMoins1);

	public abstract void learn(float[] fs);

}

package agents;

import java.util.Vector;
import robot.Action;

public abstract class Agent {
	
	public Agent() {
	}

	public abstract Action decide(Vector<Float> resultsTMoins1);

	public abstract void learn(Vector<Float> resultsT);

}

package agents;

import java.util.Vector;

import agent_developpemental.Data;
import robot.Action;

public abstract class Agent {
	
	public Agent() {
	}

	public abstract Action decide(Data resultsTMoins1);

	public abstract void learn(Vector<Float> resultsT);

}

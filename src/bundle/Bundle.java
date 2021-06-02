package bundle;

import agents.AgentDeveloppemental;
import environment.Direction;
import main.Main;
import robot.Action;

public class Bundle {
	
	
	AgentDeveloppemental agent;
	
	
	Bundle(String agentFile, int nb_sensors){
		int input_size = ((Main.colors.length + Direction.values().length - 1) * nb_sensors + 1) * Action.values().length;
		agent = new AgentDeveloppemental(0, input_size, Action.values().length, agentFile);
	}
	
	public int getNbEntries() {
		return agent.getPrimaries().length + agent.getSecondaries().length;
	}
	
	
	public static void main(String[] args){
		
		String agentFile = "";
		int nb_sensors = 121;
		
		Bundle bundle = new Bundle(agentFile, nb_sensors);
		int nb_entries = bundle.getNbEntries();
		
		
		
		for (int i=0; i<nb_entries; i++) {
			
		}
	}
}

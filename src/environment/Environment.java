package environment;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import main.Main;
import objects.Robot;

public class Environment {

	//tactile properties
	public enum Touch{EMPTY,FOOD,HARD};

	// visual properties
	public static final Color FIELD_COLOR 	= Color.white; 
	public static final Color ROBOT_COLOR 	= new Color(50,50,50);
	public static final Color WALL1       	= new Color(  0,128,  0);
	public static final Color FISH1       	= new Color(150,128,255);
	public static final Color FISH2       	= new Color(250,100,100);

	private List<InterfaceAgentRobot> agents_list;
	private int nb_agents, step;
	private _2DMap map;

	public Environment(String env_layout) {

		step = 0;
		nb_agents = 0;
		agents_list = new ArrayList<InterfaceAgentRobot>(nb_agents);

		// create map from file "env_layout"
		try{
			map = new _2DMap(this, env_layout);
		} catch (Exception e) {
			System.out.println("Error: map width is not consistent");
		}
	}

	public void step() {
		if (step % 100 == 0) {
			System.out.println(
					"==================================================================== step " +
							step +
					" ===================================================================");
			/*if (step != 0 && step % 1000 == 0) {
				for (InterfaceAgentRobot inter: agents_list) inter.updateNeurons();
			}*/
		}

		map.reset();
		for (InterfaceAgentRobot ag: agents_list) {
			ag.commandRobot();
		}
		map.solve_conflicts();
		for (InterfaceAgentRobot ag: agents_list) {
			ag.getResults();
		}

		step += 1;
	}

	@SuppressWarnings("unused")
	public void addAgent(Robot rob) {
		nb_agents += 1;
		if (Main.load_path == null)  agents_list.add(new InterfaceAgentRobot(rob));
		else {
			agents_list.add(new InterfaceAgentRobot(rob, Main.load_path + "agent_" + (nb_agents-1) + ".txt"));
		}
	}

	public _2DMap getMap() {return map;}

	public int getStep() {
		return step;
	}

	public int getNbAgents() {
		return agents_list.size();
	}

	public InterfaceAgentRobot getInterface(int id) {
		return agents_list.get(id);
	}

	public void saveAgents() {
		for (int i=0; i<agents_list.size(); i++) {
			String file_name = "ressources/agents/agent_" + i + ".txt";
			agents_list.get(i).save(file_name);
		}
	}
}

package main;

import java.awt.Color;
import environment.Environment;
import view.ControlWindow;


public class Main {

	public Environment env;
	public boolean run, pause, step;
	public ControlWindow controlView;

	// program arguments (change them here)
	private static final String env_path 			= "ressources/maps/" + "results_20x20" + ".txt";	// path to the file containing the environment layout
	public static final String load_path 			= "ressources/agents/";// + "agent_0" + ".txt";
	//public static final String load_path 			= null;
	public static final int trace_size 				= 10;	// number of previous actions of the robot displayed
	public static final int robot_vision_range 		= 5;	// distance at which the robot is able to see
	public static final float learning_rate			= 0.01f; // learning rate of the agent's perceptron
	public static final Color[] colors              = {Environment.FISH2,
														Environment.WALL1,
														//Environment.FIELD_COLOR,
														Environment.FISH1,
														Environment.ROBOT_COLOR};	

	public Main() {

		// create environment and control panel
		env = new Environment(env_path);
		controlView = new ControlWindow("Control panel", this);
		controlView.pack();
		controlView.setVisible(true);

		// launch simulation loop
		run = true;
		pause = true;
		step = false;
		while (run) {
			if (pause && !step) {
				try {
					Thread.sleep(200);
				} catch (Exception e) {}
			} else {
				env.step();	
				step = false;
				controlView.updateSlaves();
			}
		}

	}

	public void save() {
		env.saveAgents();
	}

	public static void main(String[] args){
		new Main();	
	}
}

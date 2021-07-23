package main;

import java.awt.Color;
import environment.Environment;
import view.ControlWindow;


public class Main {

	public Environment env;
	public boolean run, pause, step;
	public ControlWindow controlView;

	// program arguments (change them here)
	public static final boolean make_env_trace 		= true;
	private static final String env_path 			= "ressources/maps/" + "board_16x16_1" + ".txt";	// path to the file containing the environment layout
	//public static final String load_path 			= "ressources/agents/730k_R=4/";// + "agent_0" + ".txt";
	public static final String load_path 			= null;
	public static final String env_trace			= "ressources/trace/" + "new_trace_3_agents.txt";	// path to the file where the environment trace will be stored
	
	public static final int trace_size 				= 10;	// number of previous actions of the robot displayed
	public static final int robot_vision_range 		= 4;	// distance at which the robot is able to see
	public static final float learning_rate			= 0.01f; // learning rate of the agent's perceptron
	public static final Color[] colors              = {Environment.FISH2,
														Environment.WALL1,
														//Environment.FIELD_COLOR,
														Environment.FISH1,
														Environment.ROBOT_COLOR};	

	public Main() {

		int max_frames_per_sec = 10000;
		
		// create environment and control panel
		env = new Environment(env_path);
		controlView = new ControlWindow("Control panel", this);
		controlView.pack();
		controlView.setVisible(true);

		int min_time_between_frames = (int)(Math.floor(1000 / max_frames_per_sec));
		
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
				long start = System.currentTimeMillis();
				env.step();	
				long end = System.currentTimeMillis();
				if (end-start < min_time_between_frames) { 
					try {
						Thread.sleep(min_time_between_frames - (end-start));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
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

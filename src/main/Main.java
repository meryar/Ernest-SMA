package main;

import java.awt.Color;
import environment.Environment;
import view.ControlView;
import view.ControlWindow;


public class Main {
	
	public Environment env;
	public boolean run, pause, step;
	public ControlWindow controlView;
	
	// program arguments (change them here)
	private static final String env_path 			= "ressources/maps/test_10x10.txt";	// path to the file containing the environment layout
	public static final int trace_size 				= 10;	// number of previous actions of the robot displayed
	public static final int robot_vision_range 		= 1;	// distance at which the robot is able to see
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
			/*
			while (pause) {
				try {
					Thread.sleep(200);
				} catch (Exception e) {}
			}*/
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
	
	public static void main(String[] args){
		new Main();	
	}
}

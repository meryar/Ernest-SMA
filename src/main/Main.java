package main;

import java.awt.Color;

import environment.Environment;


public class Main {
	
	public Environment env;
	public boolean run;
	
	// program arguments (change them here)
	private static final String env_path 			= "ressources/maps/test_10x10.txt";	// path to the file containing the environment layout
	public static final int trace_size 				= 10;	// number of previous actions of the robot displayed
	public static final int robot_vision_range 		= 2;	// distance at which the robot is able to see
	public static final float learning_rate			= 0.01f; // learning rate of the agent's perceptron
	public static final Color[] colors              = {Environment.FISH2,
													   Environment.WALL1,
													   //Environment.FIELD_COLOR,
													   Environment.FISH1,
													   Environment.ROBOT_COLOR};	

	public Main() {
		
		// create environment
		env = new Environment(env_path);
		
		// launch simulation loop
		run = true;
		while (run) {
			env.step();	
		}
		
		
	}
	
	public static void main(String[] args){
		new Main();	
	}
}

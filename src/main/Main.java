package main;

import environment.Environment;


public class Main {
	
	public Environment env;
	public boolean run;
	
	// program arguments (change them here)
	private static final String env_path 			= "ressources/maps/Board_16x16_1.txt";	// path to the file containing the environment layout
	public static final int trace_size 				= 10;	// number of previous actions of the robot displayed
	public static final double robot_vision_range 	= 5;	// distance at which the robot is able to see
	public static final int nb_colors               = 5;	// number of color than the robot will be able to see (1 per object type)
	
	public Main() {
		
		// create environment (agents are created by the environment)
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

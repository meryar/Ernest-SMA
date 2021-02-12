package environment;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;

import agents.Agent;
import main.Main;
import view.View;

public class Environment {
	
	//tactile properties
	public enum Touch{EMPTY,BIGFOOD,FOOD,HARD};
	
	// visual properties
	public static final Color FIELD_COLOR 	= Color.white; 
	public static final Color ROBOT_COLOR 	= new Color(50,50,50);
	public static final Color WALL1       	= new Color(  0,128,  0);
	//public static final Color WALL2       	= new Color(  0,230, 92);
	//public static final Color ALGA1       	= new Color(220,50, 50);
	//public static final Color ALGA2       	= new Color( 46,230,  0);
	public static final Color FISH1       	= new Color(150,128,255);
	public static final Color FISH2       	= new Color(250,100,100);
	//public static final Color TILE			= new Color(200,50,50);
	//public static final Color BOX			= new Color(200,200,0);
	
	// bloc types
	public static Block empty=new Block(Touch.EMPTY, FIELD_COLOR,"empty",false);
	public static Block wall =new Block(Touch.HARD , WALL1,"wall1", true);
	//public static Block wall2=new Block(Touch.HARD , WALL2,"wall2", true);
	//public static Block alga1=new Block(Touch.SMOOTH,ALGA1,"alga1", true);
	//public static Block alga2=new Block(Touch.SMOOTH,ALGA2,"alga2", true);
	public static Block fish1=new Block(Touch.FOOD ,"small_fish.jpg","fish1", true, Direction.NORTH);
	public static Block fish2=new Block(Touch.FOOD  ,"big_fish.png","fish2", true, Direction.NORTH);
	//public static Block tile =new Block(Touch.SMOOTH,TILE,"tile",true);

	
	private List<Agent> agents_list;
	int nb_agents;
	private _2DMap map;
	private int env_height;
	private int env_width;
	private View main_frame;

	public Environment(String env_layout) {
		
		nb_agents = 0;
		agents_list = new ArrayList<Agent>(nb_agents);
		
		// create map from file "env_layout"
		try{
			map = new _2DMap(this, env_layout);
		} catch (Exception e) {
			System.out.println("Error: map width is not consistent");
		}
		
		main_frame = new View(this, map, env_height, env_width,"window");
		
		main_frame.pack();
        main_frame.setVisible(true);
        
        
	}

	public void step() {
		// TODO Auto-generated method stub
		//main_frame.update();
	}

	public void addAgent(Robot rob) {
		nb_agents += 1;
		agents_list.add(new Agent(rob));
		
	}
	
	public void setDimentions(int height, int width) {
		env_height = height;
		env_width = width;
	}
	
	public _2DMap getMap() {return map;}

}

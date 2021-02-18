package view;

import java.awt.Color;
import java.awt.Desktop.Action;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JPanel;

import main.Main;

public class RobotViewPanel extends JPanel{

	private static final long serialVersionUID = -2141529419693098129L;
	
	private int pan_height, pan_width;
	private Vector<Float> entryToDisplay;
	private int nb_sensors, nb_colors, nb_interactions;

	public RobotViewPanel(int pan_height_, int pan_width_, int nb_sensors_) {
		pan_height = pan_height_;
		pan_width = pan_width_;
		nb_sensors = nb_sensors_;
		nb_colors = Main.nb_colors;
		nb_interactions = Action.values().length;
	}
	
	@Override
	public void paintComponent(Graphics g){
		int interaction;
		
		for (int i=0; i<nb_interactions; i++) {
			//if (Action.values())
		}
		
		g.setColor(new Color((int) (Math.random()* 16777214)));
		//g.setColor(Color.white);
		g.fillRect(0, 0, pan_width, pan_height);
	}

	public void setEntryToDisplay(Vector<Float> entryToDisplay) {
		this.entryToDisplay = entryToDisplay;
	}
	
}

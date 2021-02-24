package view;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import agent_developpemental.Neuron;
import agent_developpemental.Perceptron;
import main.Main;
import robot.Action;

public class PerceptronPanel extends JPanel{

	private static final long serialVersionUID = 1L;

	private int pan_height, pan_width, screen_x_offset, screen_y_offset;
	private Perceptron perceptron;
	private int nb_sensors;
	
	public PerceptronPanel(Perceptron perceptron_, int pan_height_, int pan_width_) {
		pan_height = pan_height_;
		pan_width = pan_width_;
		perceptron = perceptron_;
		screen_y_offset = (int) (pan_height / 2);
		screen_x_offset = (int) (pan_width / Action.values().length);
		nb_sensors = (int) Math.pow(Main.robot_vision_range*2 + 1, 2);
	}

	@Override
	public void paintComponent(Graphics g){
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, pan_width, pan_height);
		
		int neuron_offset = (int) (Action.values().length * Main.colors.length * nb_sensors);
		int x_offset = 0;
		int y_offset = screen_y_offset;
		
		for (int i=0; i<Action.values().length; i++) {
			x_offset = i * screen_x_offset;
			
			Neuron neuron = perceptron.getNeurons().get(neuron_offset + i);
			for (int s=0; s<nb_sensors; s++) {
				int x = x_offset + (screen_x_offset / (Main.robot_vision_range*2 + 1)) * s % (Main.robot_vision_range*2 + 1);
				int y = ((screen_y_offset / (Main.robot_vision_range*2 + 1)) * s % (Main.robot_vision_range*2 + 1));
				//g.setColor();
			}
		}
	}
	
}

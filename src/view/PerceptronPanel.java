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
		
		int offset_to_prim = (int) (Action.values().length * Main.colors.length * nb_sensors);
		int offset = (int) (Main.colors.length * nb_sensors);
		
		int big_x_offset = 0;
		int big_y_offset = screen_y_offset;
		int small_x_offset = (screen_x_offset / (Main.robot_vision_range*2 + 1));
		int small_y_offset = (screen_y_offset / (Main.robot_vision_range*2 + 1));
		
		for (int i=0; i<Action.values().length; i++) {
			big_x_offset = i * screen_x_offset;
			
			Neuron neuron = perceptron.getNeurons().get(offset_to_prim + i);
			for (int s=0; s<nb_sensors; s++) {
				int x = big_x_offset + small_x_offset * (s % (Main.robot_vision_range*2 + 1));
				int y = (int) (small_y_offset * (Math.floor(s / (Main.robot_vision_range*2 + 1))));

				float weight0 = normalize(neuron.getWeights().get(i * offset + 0 * nb_sensors + s), neuron.max_abs_weight);
				float weight1 = normalize(neuron.getWeights().get(i * offset + 1 * nb_sensors + s), neuron.max_abs_weight);
				float weight2 = normalize(neuron.getWeights().get(i * offset + 2 * nb_sensors + s), neuron.max_abs_weight);
				//System.out.println("normalized weigths: " + weight0 + " " + weight1 + " " + weight2);
				
				g.setColor(new Color(
						weight0,
						weight1,
						weight2
						));
				g.fillRect(x, y, small_x_offset, small_y_offset);
			}
		}
	}

	private float normalize(Float n, float max) {
		return (n + max) / (2*max);
	}
	
}

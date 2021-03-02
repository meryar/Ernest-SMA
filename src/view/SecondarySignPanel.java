package view;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import agent_developpemental.Neuron;
import agent_developpemental.Perceptron;
import main.Main;
import robot.Action;

public class SecondarySignPanel extends JPanel{
	
	private static final long serialVersionUID = -2888209565456847149L;
	
	private Perceptron perceptron;
	private int pan_height, pan_width, screen_x_offset, screen_y_offset, sensor_id;

	public SecondarySignPanel(Perceptron perceptron_, int pan_height_, int pan_width_) {
		
		perceptron = perceptron_;
		pan_height = pan_height_;
		pan_width = pan_width_;
		sensor_id = -1;
		screen_y_offset = (int) (pan_height / Main.colors.length);
		screen_x_offset = (int) (pan_width / Action.values().length);
	}
	
	@Override
	public void paintComponent(Graphics g){
		if (sensor_id > 0) {
			int nb_sensors = (int) Math.pow(Main.robot_vision_range*2 +1, 2);
			int offset = nb_sensors * Main.colors.length;
			int small_x_offset = (screen_x_offset / (Main.robot_vision_range*2 + 1));
			int small_y_offset = (screen_y_offset / (Main.robot_vision_range*2 + 1));
			
			
			g.setColor(Color.white);
			g.fillRect(0, 0, pan_width, pan_height);
			
			for (int color=0; color<Main.colors.length; color++) {
				for (int act=0; act<Action.values().length; act++) {
					Neuron neuron = perceptron.getNeurons().get(
							(int) (act * Main.colors.length * nb_sensors
							+ color * nb_sensors));
					
					for (int sensor_x =0; sensor_x<Main.robot_vision_range*2+1; sensor_x++) {
						for (int sensor_y =0; sensor_y<Main.robot_vision_range*2+1; sensor_y++) {
							int x = screen_x_offset + small_x_offset * (sensor_id % (Main.robot_vision_range*2 + 1));
							int y = (int) (small_y_offset * (Math.floor(sensor_id / (Main.robot_vision_range*2 + 1))));
	
							
							float weight0Pos = normalize(neuron.getWeights().get(act * offset + 0 * nb_sensors + sensor_id), neuron.max_weight);
							float weight1Pos = normalize(neuron.getWeights().get(act * offset + 1 * nb_sensors + sensor_id), neuron.max_weight);
							float weight2Pos = normalize(neuron.getWeights().get(act * offset + 2 * nb_sensors + sensor_id), neuron.max_weight);
							
							g.setColor(new Color(
									weight0Pos,
									weight1Pos,
									weight2Pos
									));
							g.fillRect(x, y, small_x_offset, small_y_offset);
						}
					}
				}
			}
		}
	}

	public void setSensor(int sensor_id_) {
		sensor_id = sensor_id_;
	}
	
	private float normalize(float n, float max) {
		if(Float.isInfinite(Math.max(0, n/max))){
			System.err.println(n + " / " + max + " = infinity");
		}
		//System.out.println(Math.max(0, n/max));
		return Math.max(0, n/max);
	}
	
	private float reverseNormalize(Float n, float min) {
		return Math.max(0, n/min);
	}

}

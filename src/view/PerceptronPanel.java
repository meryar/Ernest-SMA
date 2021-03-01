package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import agent_developpemental.Neuron;
import agent_developpemental.Perceptron;
import main.Main;
import robot.Action;

public class PerceptronPanel extends JPanel implements ActionListener, MouseListener{

	private static final long serialVersionUID = 1L;

	private int pan_height, pan_width, screen_x_offset, screen_y_offset;
	private Perceptron perceptron;
	private int nb_sensors;
	private int selected;
	private JComboBox selection;
	
	public PerceptronPanel(Perceptron perceptron_, int pan_height_, int pan_width_) {
		pan_height = pan_height_;
		pan_width = pan_width_;
		perceptron = perceptron_;
		screen_y_offset = (int) (pan_height / 2);
		screen_x_offset = (int) (pan_width / Action.values().length);
		nb_sensors = (int) Math.pow(Main.robot_vision_range*2 + 1, 2);
		selected = 0;

		selection = new JComboBox();
		selection.addItem("Move forward");
		selection.addItem("Bump");
		selection.addItem("Fight");
		selection.addItem("Eat");
		selection.addItem("Feast");
		selection.addItem("Rotate left");
		selection.addItem("Rotate right");
		
		selection.addActionListener(this);
		addMouseListener(this);
		add(selection);
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
			System.out.println( "Interaction " + Action.values()[i]
					+ ": max: " + neuron.max_weight 
					+ " min: " + neuron.min_weight 
					+ " max abs: " + neuron.max_abs_weight
					+ " bias: " + neuron.bias);
			for (int s=0; s<nb_sensors; s++) {
				int x = big_x_offset + small_x_offset * (s % (Main.robot_vision_range*2 + 1));
				int y = (int) (small_y_offset * (Math.floor(s / (Main.robot_vision_range*2 + 1))));

				if(neuron.getWeights().get(i * offset + 0 * nb_sensors + s) == neuron.max_weight) {System.err.println("OK");}
				float weight0Pos = normalize(neuron.getWeights().get(selected * offset + 0 * nb_sensors + s), neuron.max_weight);
				float weight1Pos = normalize(neuron.getWeights().get(selected * offset + 1 * nb_sensors + s), neuron.max_weight);
				float weight2Pos = normalize(neuron.getWeights().get(selected * offset + 2 * nb_sensors + s), neuron.max_weight);

				System.out.println("s " + s + " pos: w0 " + weight0Pos + "   w1 " + weight1Pos + "   w2 " + weight2Pos);
				
				g.setColor(new Color(
						weight0Pos,
						weight1Pos,
						weight2Pos
						));
				g.fillRect(x, y, small_x_offset, small_y_offset);
				
				float weight0Neg = reverseNormalize(neuron.getWeights().get(selected * offset + 0 * nb_sensors + s), neuron.min_weight);
				float weight1Neg = reverseNormalize(neuron.getWeights().get(selected * offset + 1 * nb_sensors + s), neuron.min_weight);
				float weight2Neg = reverseNormalize(neuron.getWeights().get(selected * offset + 2 * nb_sensors + s), neuron.min_weight);
				
				System.out.println("s " + s + " neg: w0 " + weight0Neg + "   w1 " + weight1Neg + "   w2 " + weight2Neg);
				
				g.setColor(new Color(
						weight0Neg,
						weight1Neg,
						weight2Neg
						));
				g.fillRect(x, y + screen_y_offset, small_x_offset, small_y_offset);
			}
		}
	}

	private float normalize(float n, float max) {
		if(Float.isInfinite(Math.max(0, n/max))){
			System.err.println(n + " / " + max + " = infinity");
		}
		System.out.println(Math.max(0, n/max));
		return Math.max(0, n/max);
	}
	
	private float reverseNormalize(Float n, float min) {
		return Math.max(0, n/min);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		selected = selection.getSelectedIndex();
		repaint();
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point location = new Point(e.getX(), e.getY());
		System.out.println(location);
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}

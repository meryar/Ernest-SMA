package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;

import agent_developpemental.Perceptron;

public class SecondarySignView extends JFrame{

	private static final long serialVersionUID = 3209608584521517713L;

	private SecondarySignPanel panel;
	private int pan_height, pan_width;
	
	public SecondarySignView(Perceptron perceptron) {
		super("Secondary signature view");
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		pan_height = (int) (screenSize.getHeight() * 0.8);
		pan_width = (int) (screenSize.getWidth() * 0.95);
	
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel=new SecondarySignPanel(perceptron, pan_height, pan_width);
    	this.setContentPane(panel);
    	
		JLabel emptyLabel = new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(pan_width,pan_height));
        this.getContentPane().add(emptyLabel, BorderLayout.CENTER);
	
	}
	
	public void updateView() {
		panel.repaint();
	}
	
	public void setSensor(int sensor_id) {
		panel.setSensor(sensor_id);
	}
}

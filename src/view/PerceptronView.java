package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;

import agent_developpemental.Perceptron;

public class PerceptronView extends JFrame{

	private static final long serialVersionUID = -7507304262629213305L;
	
	private int pan_height, pan_width;
	private PerceptronPanel panel;
	
	public PerceptronView(String name, Perceptron perceptron) {
		super(name);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		pan_height = (int) (screenSize.getHeight() * 0.9);
		pan_width = (int) (screenSize.getWidth() * 0.6);
	
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel=new PerceptronPanel(perceptron, pan_height, pan_width);
    	this.setContentPane(panel);
    	
		JLabel emptyLabel = new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(pan_width,pan_height));
        this.getContentPane().add(emptyLabel, BorderLayout.CENTER);
	}
}

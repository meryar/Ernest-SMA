package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class RobotView extends JFrame{

	private static final long serialVersionUID = 702064571452051235L;
	
	private int pan_height, pan_width;
	private RobotViewPanel panel;
	
	public RobotView(String name, int nb_sensors) {
		super(name);
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		pan_height = (int) (screenSize.getHeight() * 0.4);
		pan_width = (int) (screenSize.getWidth() * 0.2);
	
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel=new RobotViewPanel(pan_height, pan_width, nb_sensors);
    	this.setContentPane(panel);
    	
		JLabel emptyLabel = new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(pan_width,pan_height));
        this.getContentPane().add(emptyLabel, BorderLayout.CENTER);
	}
	
	public void updateView(Vector<Float> AgentEntry) {
		panel.setEntryToDisplay(AgentEntry);
		panel.repaint();
	}
}

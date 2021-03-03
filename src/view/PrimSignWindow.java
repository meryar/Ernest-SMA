package view;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import agent_developpemental.Perceptron;
import agents.AgentDeveloppemental;
import environment.Environment;
import environment.InterfaceAgentRobot;
import robot.Action;

public class PrimSignWindow extends SlaveView{

	private static final long serialVersionUID = 1L;
	
	private final int box_height = 30; 
	
	private PrimSignPane panel;
	
	public PrimSignWindow(String name) {
		super(name);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// setting starting screen size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int window_base_height = (int) (screenSize.getHeight() * 0.5);
		int window_base_width = (int) (screenSize.getWidth() * 0.9);
		this.setBounds(0, 0, window_base_width, window_base_height);

		// creating pane
		panel = new PrimSignPane(box_height+5);
		this.setContentPane(panel);
		
		// selection box
		JComboBox select = new JComboBox();
	    select.setBounds(window_base_width/2,0,95,box_height);  
	    for (Action act: Action.values()) {
	    	select.addItem(act);
	    }
	    select.addActionListener(
	    		new java.awt.event.ActionListener(){
	    			public void actionPerformed(ActionEvent e) {
	    				System.out.println("Now displaying signature of primary interaction " + select.getSelectedItem());
	    				panel.setFocus(select.getSelectedIndex());
	    				repaint();
	    		    }
	    		});
	    add(new JLabel("Signatures for interaction: "));
	    add(select); 
	    
	    // bias display
	    add(new JLabel("    bias: " + 0.0));
	}

	@Override
	public void setFocus(int ID, Environment env) {
		InterfaceAgentRobot agent = env.getInterface(ID);
		panel.setPerceptron(agent);
	}

}

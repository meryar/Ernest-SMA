package view;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import main.Main;

public class ControlWindow extends AbstractView{

	private static final long serialVersionUID = -4609180891346436121L;
	
	private Main main;
	private EnvWindow env_display;

	public ControlWindow(String name, Main main_) {
		super(name);
		main = main_;
		
		this.setAlwaysOnTop(true);
		
		// display of the window
		setLayout(new FlowLayout());
		
		// button starting and pausing simulation
		JButton pause = new JButton("play/pause");  
		pause.setBounds(50,100,95,30);  
		pause.addActionListener(
				new java.awt.event.ActionListener(){
					public void actionPerformed(ActionEvent e) {
						main.pause = !main.pause;
					}
				});
	    add(pause); 
	    
	    // button launching simulation for a single step
	    JButton step = new JButton("step");  
	    step.setBounds(150,100,95,30);  
	    step.addActionListener(
	    		new java.awt.event.ActionListener(){
	    			public void actionPerformed(ActionEvent e) {
	    				main.step = true;
	    		    }
	    		});
	    add(step);
	    
	    // combo box selecting wich agent/robot couple will be the focus of the other windows
	    JComboBox select = new JComboBox();  
	    select.setBounds(150,100,95,30);  
	    for (int i=0; i<main.env.getNbAgents(); i++) {
	    	select.addItem("Agent " + i);
	    }
	    select.addActionListener(
	    		new java.awt.event.ActionListener(){
	    			public void actionPerformed(ActionEvent e) {
	    				System.out.println("Now displaying internal state of agent " + select.getSelectedIndex());
	    				updateFocus(select.getSelectedIndex());
	    		    }
	    		});
	    add(new JLabel(" focus is on:"));
	    add(select); 
	    
	    
	    // creating sub windows
	    env_display = new EnvWindow("Environment", main.env.getMap());
	}

	/**
	 * Method updating the focused agent or robot of the sub windows.
	 * 
	 * @param selectedIndex: ID of the robot/agent couple to set as focus
	 */
	protected void updateFocus(int selectedIndex) {
		
		
	}

}

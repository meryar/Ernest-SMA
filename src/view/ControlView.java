package view;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import main.Main;

public class ControlView extends JFrame{

	private static final long serialVersionUID = 1L;
	
	public ControlView(String name, Main main) {
		super(name);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.setLayout(new FlowLayout());
		
		JButton pause = new JButton("play/pause");  
		pause.setBounds(50,100,95,30);  
		pause.addActionListener(
				new java.awt.event.ActionListener(){
					public void actionPerformed(ActionEvent e) {
						main.pause = !main.pause;
					}
				});
	    add(pause); 
	    
	    JButton step = new JButton("step");  
	    step.setBounds(150,100,95,30);  
	    step.addActionListener(
	    		new java.awt.event.ActionListener(){
	    			public void actionPerformed(ActionEvent e) {
	    				main.step = true;
	    		    }
	    		});
	    add(step);  
	    
	    JButton stop = new JButton("close");  
	    stop.setBounds(150,100,95,30);  
	    stop.addActionListener(
	    		new java.awt.event.ActionListener(){
	    			public void actionPerformed(ActionEvent e) {
	    				System.exit(0);
	    		    }
	    		});
	    add(stop); 
	}

}

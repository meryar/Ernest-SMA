package view;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import main.Main;

public class ControlView extends JFrame{

	private static final long serialVersionUID = 1L;
	
	public ControlView(String name, Main main) {
		super(name);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton b=new JButton("play/pause");  
	    b.setBounds(50,100,95,30);  
	    b.addActionListener(new java.awt.event.ActionListener(){
	    	   public void actionPerformed(ActionEvent e) {
	    		      main.pause = !main.pause;
	    		   }
	    		});
	    add(b);  
	}

}

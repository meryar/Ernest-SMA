package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JLabel;
import environment.Environment;
import environment._2DMap;

public class View extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private int pan_height,pan_width;
	private int env_height,env_width;
	private ViewPanel panel; 

	public View() {
		
	}

	public View(Environment env_, _2DMap map, int env_height_, int env_width_, String name) {
		super(name);
		env_height = env_height_;
		env_width = env_width_;
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		pan_height = (int) (screenSize.getHeight() * 0.9);
		pan_width = (int) (screenSize.getWidth() * 0.6);
		
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel=new ViewPanel(map, pan_height, pan_width, env_height, env_width);
    	this.setContentPane(panel);
    	
		JLabel emptyLabel = new JLabel("");
        emptyLabel.setPreferredSize(new Dimension(pan_width,pan_height));
        this.getContentPane().add(emptyLabel, BorderLayout.CENTER);
        
	}
	
	public void updateView() {
		panel.repaint();
	}

}

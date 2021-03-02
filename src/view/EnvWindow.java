package view;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import environment._2DMap;

public class EnvWindow extends AbstractView{
	
	private _2DMap map;
	private EnvPane panel;

	public EnvWindow(String name, _2DMap map_) {
		super(name);

		map = map_;
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// setting starting screen size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int window_base_height = (int) (screenSize.getHeight() * 0.9);
		int window_base_width = (int) (screenSize.getWidth() * 0.6);
		this.setBounds(0, 0, window_base_width, window_base_height);
		
		// creating pane
		panel = new EnvPane();
		this.setContentPane(panel);
	}

}

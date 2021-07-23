package view;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import environment.Environment;
import environment._2DMap;

public class EnvWindow extends SlaveView{

	private static final long serialVersionUID = 1L;

	private EnvPane panel;

	public EnvWindow(String name, Environment env) {
		super(name);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// setting starting screen size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int window_base_height = (int) (screenSize.getHeight() * 0.9);
		int window_base_width = (int) (screenSize.getWidth() * 0.6);
		this.setBounds(0, 0, window_base_width, window_base_height);

		// creating pane
		panel = new EnvPane(env);
		this.setContentPane(panel);
	}

	@Override
	public void setFocus(int ID, Environment env) {
		panel.setFocus(ID);
	}

}

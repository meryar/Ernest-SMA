package view;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import environment.Environment;

public class CameraWindow extends SlaveView{

	private static final long serialVersionUID = 1L;

	private CameraPane panel;

	public CameraWindow(String name) {
		super(name);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// setting starting screen size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int window_base_height = (int) (screenSize.getHeight() * 0.5);
		int window_base_width = (int) (screenSize.getWidth() * 0.9);
		this.setBounds(0, 0, window_base_width, window_base_height);

		// creating pane
		panel = new CameraPane();
		this.setContentPane(panel);
	}

	@Override
	public void setFocus(int ID, Environment env) {
		panel.setInterface(env.getInterface(ID));
	}

}

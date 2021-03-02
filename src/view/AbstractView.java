package view;

import javax.swing.JFrame;

public abstract class AbstractView extends JFrame{

	private static final long serialVersionUID = 693979246760884493L;

	public AbstractView(String name) {
		super(name);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		pack();
		setVisible(true);
	}
}

package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

import agents.AgentDeveloppemental;
import environment.InterfaceAgentRobot;

public class EntryPane extends JPanel{

	private static final long serialVersionUID = 1L;
	private InterfaceAgentRobot interface_;
	private float[] data;
	private JLabel label;
	

	public EntryPane() {
		label = new JLabel("start simulation");
		add(label);
		label.setSize(this.getSize());
	}
	
	@Override
	public void paintComponent(Graphics g){
		Dimension dim = this.getSize();
		g.setColor(Color.white);
		g.fillRect(0, 0, dim.width, dim.height);
		
		if (interface_ != null && ((AgentDeveloppemental)interface_.getAgent()).getLastPerception() != null) {
			updateData();
			
			label.setBounds(0, 0, dim.width, dim.height);
			int charPerLine = dim.width / 40;
			String output = "<html>";
			for (float value: data) { 
				output += (int)value;
				if(output.length() % charPerLine == 0) {
					output += " ";
				}
			}
			output += "</html>";
			label.setText(output);
			System.out.println(output);
		}
	}
	
	private void updateData() {
		data = ((AgentDeveloppemental) interface_.getAgent()).getLastPerception();
	}

	public void setInterface(InterfaceAgentRobot robot_interface) {
		interface_ = robot_interface;
	}
}

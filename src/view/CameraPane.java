package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JPanel;

import agents.AgentDeveloppemental;
import environment.Direction;
import environment.InterfaceAgentRobot;
import main.Main;
import robot.Action;

public class CameraPane extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private InterfaceAgentRobot interface_;
	private Vector<Float> data;
	private int offset_screen_x, offset_screen_y;

	public CameraPane() {
		offset_screen_x = 10;
		offset_screen_y = 3;
	}
	
	@Override
	public void paintComponent(Graphics g){
		if (interface_ != null && ((AgentDeveloppemental)interface_.getAgent()).getLastPerception() != null) {
			updateData();
			int offsetToInter = data.size() / Action.values().length - 1;
			int offsetToColor = offsetToInter / (Main.colors.length + Direction.values().length -1);
			
			double sensor_map_side = Math.sqrt(interface_.getRobot().getSensorNb());
			
			int camera_height = (int) sensor_map_side;
			int camera_width = (int) sensor_map_side;
			
			int nb_actions = Action.values().length;
			
			Dimension pane_size = this.getSize();
			g.setColor(Color.white);
			g.fillRect(0, 0, pane_size.width, pane_size.height);

			int screen_width = (int) (pane_size.width / nb_actions 
									- offset_screen_x);
			int screen_height = (int) (pane_size.height / 2 
									- offset_screen_y);
			int offset_x = (int) (screen_width / camera_width);
			int offset_y = (int) (screen_height / camera_height);
			
			for (int act=0; act<nb_actions; act++) {
				int screen_x = act * (screen_width + offset_screen_x);
				
				for (int line=0; line<camera_height; line++) {
					for (int column=0; column<camera_width; column++) {
						int x = screen_x + column * offset_x;
						int y = line * offset_y;
						
						Color color1 = new Color(
								data.get(act * offsetToInter + 0 * offsetToColor + line*camera_width + column),
								data.get(act * offsetToInter + 1 * offsetToColor + line*camera_width + column),
								data.get(act * offsetToInter + 2 * offsetToColor + line*camera_width + column)
								);
						
						Color color2 = new Color(
								data.get(act * offsetToInter + 3 * offsetToColor + line*camera_width + column),
								data.get(act * offsetToInter + 4 * offsetToColor + line*camera_width + column),
								data.get(act * offsetToInter + 5 * offsetToColor + line*camera_width + column)
								);

						g.setColor(color1);
						g.fillRect(x, y, offset_x, offset_y);
						
						g.setColor(color2);
						g.fillRect(x, screen_height + offset_screen_y + y, offset_x, offset_y);
					}
				}
			}
		}
	}

	private void updateData() {
		data = ((AgentDeveloppemental) interface_.getAgent()).getLastPerception();
	}

	public void setInterface(InterfaceAgentRobot robot_interface) {
		interface_ = robot_interface;
	}
}

package view;

import java.awt.Color;
import java.awt.Desktop.Action;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JPanel;

import main.Main;

public class RobotViewPanel extends JPanel{

	private static final long serialVersionUID = -2141529419693098129L;
	
	private int pan_height, pan_width;
	private Vector<Float> entryToDisplay;
	private int nb_sensors, nb_colors, nb_interactions;
	private int screen_height, x_offset,y_offset;

	public RobotViewPanel(int pan_height_, int pan_width_, int nb_sensors_) {
		pan_height = pan_height_;
		pan_width = pan_width_;
		nb_sensors = nb_sensors_;
		nb_colors = Main.colors.length;
		nb_interactions = robot.Action.values().length;
		screen_height = pan_height_/2;
		x_offset = pan_width_ / (int)(Math.floor(Main.robot_vision_range * 2));
		y_offset = screen_height / (int)(Math.floor(Main.robot_vision_range * 2));
	}
	
	@Override
	public void paintComponent(Graphics g){
		g.setColor(Color.white);
		g.fillRect(0, 0, pan_width, pan_height);
		
		if (!(entryToDisplay == null)) {
			int interaction = -1;
			int offset_to_prim = nb_colors*nb_interactions*nb_sensors;
			
			for (int i=0; i<nb_interactions; i++) {
				if (entryToDisplay.get(offset_to_prim + i).equals(1f)) {
					interaction = i;
				}
			}
			
			float[][] colorMap = new float[nb_sensors][nb_colors];
			if (interaction != -1) {
				
				for (int s=0; s<nb_sensors; s++) {
					for (int c=0; c<nb_colors; c++) {
						colorMap[s][c] = entryToDisplay.get(interaction*nb_colors*nb_sensors + c*nb_sensors + s);
					}
					g.setColor(new Color(colorMap[s][0],colorMap[s][1],colorMap[s][2]));
					g.fillRect(
							(int)((s % (2*Main.robot_vision_range)) * x_offset), 
							(int) (Math.floor(s/(Main.robot_vision_range*2))*y_offset), 
							x_offset, 
							y_offset);
					g.setColor(new Color(colorMap[s][3],colorMap[s][4],0));
					g.fillRect((int)(s % (Main.robot_vision_range*2) * x_offset), (int) (screen_height + y_offset* Math.floor(s/(Main.robot_vision_range*2))), x_offset, y_offset);
					
				}
			}
			
			
		}
	}

	public void setEntryToDisplay(Vector<Float> entryToDisplay) {
		this.entryToDisplay = entryToDisplay;
	}
	
}

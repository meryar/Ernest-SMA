package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import environment.Direction;
import environment._2DMap;
import environment.Object;

public class ViewPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	
	private int pan_height,pan_width,env_height,env_width;
	private _2DMap map;
	
	public ViewPanel(_2DMap map_, int pan_height_, int pan_width_, int env_height_, int env_width_) {
		map = map_;
		pan_height = pan_height_;
		pan_width = pan_width_;
		env_height = env_height_;
		env_width = env_width_;
	}
	
	@Override
	public void paintComponent(Graphics g){
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, pan_width, pan_height);
		
		// for all objects in environment
		for (int l=0; l< env_height; l++) {
			for (int c=0; c<env_width; c++) {
				for (Object obj: map.getMap()[l][c]) {
					// draw color or image corresponding to object
					if (obj.getVisible()) {
						drawObject(obj, g, l, c);
					}
				}
			}
		}
	}

	private void drawObject(Object obj, Graphics g2d, int line, int column) {
		int v_offset = (int)Math.floor(pan_width/env_width);
		int h_offset = (int)Math.floor(pan_height/env_height);

		int drawLocationX, drawLocationY;
		drawLocationX = column * v_offset;
		drawLocationY = line * h_offset;
		
		if (obj.getImage() == null) {
			g2d.setColor(obj.getColor());
			g2d.fillRect(drawLocationX, drawLocationY, v_offset, h_offset);
		} else{
			BufferedImage image = (BufferedImage) obj.getImage();
			double angle = directionToAngle(obj.getDirection());
			
			AffineTransform backup = ((Graphics2D) g2d).getTransform();
			AffineTransform a = AffineTransform.getRotateInstance(Math.toRadians(angle), drawLocationX + v_offset/2, drawLocationY + h_offset/2);
			((Graphics2D) g2d).setTransform(a);
			if (angle == 90 || angle == 270) {
				g2d.drawImage(image, 
						drawLocationX + v_offset/2 - h_offset/2, 
						drawLocationY + h_offset/2 - v_offset/2, 
						h_offset, v_offset, null);
			} else {
				g2d.drawImage(image, 
						drawLocationX, 
						drawLocationY, 
						v_offset, h_offset, null);
			}
			((Graphics2D) g2d).setTransform(backup);
		}
	}

	private double directionToAngle(Direction direction) {
		switch (direction) {
			case NORTH:
				return 0;
			case SOUTH:
				return 180;
			case WEST:
				return 270;
			case EAST:
				return 90;
		}
		return 0;
	}
}

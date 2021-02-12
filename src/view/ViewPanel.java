package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.List;

import javax.swing.JPanel;

import environment.Block;
import environment.Direction;
import environment._2DMap;
import environment.Object;

public class ViewPanel extends JPanel{
	
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
		
		// drawing objects in environment
		for (int l=0; l< env_height; l++) {
			for (int c=0; c<env_width; c++) {
				// TODO: draw color corresponding to object
				for (Object obj: map.getMap()[l][c]) {
					if (obj.getName() != "empty") {
						drawObject(obj, g, l, c);
					}
				}
			}
		}		
	}

	private void drawObject(Object obj, Graphics g2d, int line, int column) {
		int v_offset = Math.round(pan_width/env_width);
		int h_offset = Math.round(pan_height/env_height);
		
		if (obj.getImage() == null) {
			g2d.setColor(obj.getColor());
			g2d.fillRect(column*v_offset, line*h_offset, v_offset, h_offset);
		} else{
			
			BufferedImage image = (BufferedImage) obj.getImage();
			int drawLocationX = column*v_offset;
			int drawLocationY = line*h_offset;
			double angle = directionToAngle(obj.getDirection());
			
			AffineTransform backup = ((Graphics2D) g2d).getTransform();
			AffineTransform a = AffineTransform.getRotateInstance(Math.toRadians(angle), drawLocationX + v_offset/2, drawLocationY + h_offset/2);
			((Graphics2D) g2d).setTransform(a);
			g2d.drawImage(image, drawLocationX, drawLocationY, v_offset, h_offset, null);
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
				return -90;
			case EAST:
				return 90;
		}
		return 0;
	}
}

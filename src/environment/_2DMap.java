package environment;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import main.Main;

public class _2DMap {
	private List<Object>[][] map;
	private int height,width;
	private Environment env;
	private Map<String,Integer> object_number;
	
	public _2DMap(int height_, int width_) {
		height = height_;
		width = width_;
		map = (ArrayList<Object> [][]) new ArrayList[height][width];
		object_number = new HashMap<String,Integer>();
	}
	
	public _2DMap(Environment env_, String fileName) {
		object_number = new HashMap<String,Integer>();
		env = env_;
		
		List<String[]> file_copy = new ArrayList<String[]>();
		
		// reading and copying file
		try {
		      File myObj = new File(fileName);
		      Scanner myReader = new Scanner(myObj);
		      System.out.println("Environment layout:");
		      while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        System.out.println(data);
		        file_copy.add(data.split(" "));
		      }
		      myReader.close();
		} catch (FileNotFoundException e) {
		      System.out.println("File specified in 'Main.java' does not exist");
		      e.printStackTrace();
		}
		
		// checking that file is conform
		height = file_copy.size();
		width = file_copy.get(0).length;
		for(String[] line : file_copy) {
			if (line.length != width) throw new IllegalStateException("Width not consistent!");
		}
		
		//
		env.setDimentions(height, width);
		
		// filling map
		map = (ArrayList<Object> [][]) new ArrayList[height][width];	
		for (int l=0; l<height; l++) {
			for (int c=0; c<width; c++) {
				map[l][c]= new ArrayList<Object>();
				map[l][c].add(Environment.empty);
				try{
					map[l][c].add(0, char_reader(file_copy.get(l)[c], c, l));
				} catch (Exception e) {
					System.out.println("Error: unknown character was found and replaced with a wall: " + e.getMessage());
					object_number.compute("wall", (k,v) -> (v==null) ? 1 : v+1);
					map[l][c].add(Environment.wall);
				}
			}
		}
	}
		
		
	public Object char_reader(String tile_type, int posX, int posY) throws Exception{
		
		switch (tile_type) {
			case "w":
				object_number.compute("wall", (k,v) -> (v==null) ? 1 : v+1);
				return Environment.wall;
			case "v":
				object_number.compute("robot", (k,v) -> (v==null) ? 1 : v+1);
				Robot rob_down = new Robot(this, "robot.jpg", Environment.Touch.HARD, "robot_" + object_number.get("robot"), true, Direction.SOUTH, posX, posY, Main.robot_vision_range);
				env.addAgent(rob_down);
				return rob_down;
			case ">":
				object_number.compute("robot", (k,v) -> (v==null) ? 1 : v+1);
				Robot rob_right = new Robot(this, "robot.jpg", Environment.Touch.HARD, "robot_" + object_number.get("robot"), true, Direction.EAST, posX, posY, Main.robot_vision_range);
				env.addAgent(rob_right);
				return rob_right;
			case "^":
				object_number.compute("robot", (k,v) -> (v==null) ? 1 : v+1);
				Robot rob_up = new Robot(this, "robot.jpg", Environment.Touch.HARD, "robot_" + object_number.get("robot"), true, Direction.NORTH, posX, posY, Main.robot_vision_range);
				env.addAgent(rob_up);
				return rob_up;
			case "<":
				object_number.compute("robot", (k,v) -> (v==null) ? 1 : v+1);
				Robot rob_left = new Robot(this, "robot.jpg", Environment.Touch.HARD, "robot_" + object_number.get("robot"), true, Direction.WEST, posX, posY, Main.robot_vision_range);
				env.addAgent(rob_left);
				return rob_left;
				
			case "-":
				return Environment.empty;
						
			case "*":
				object_number.compute("small_fish", (k,v) -> (v==null) ? 1 : v+1);
				return Environment.fish1;
				
			case "°":
				object_number.compute("big_fish", (k,v) -> (v==null) ? 1 : v+1);
				return Environment.fish2;
				
			default:
				throw new IllegalStateException(tile_type);
		}
	}
	
	public boolean moveObject(String name, Point startPos, Point endPos) {
		if (!isLegalPosition(endPos)) {return false;}
		
		for (Object obj: map[startPos.y][startPos.x]) {
			if (obj.getName().equals(name)) {
				System.out.println(true);
				map[endPos.y][endPos.x].add(obj);
				map[startPos.y][startPos.x].remove(obj);
				return true;
			}
		}
		return false;
	}
	
	public boolean randomMove(String name, Point startPos) {
		Point newPos = new Point();
		int count = (int) Math.floor(width * height / 5);
		do {
			newPos.x = (int)(Math.random() * (width + 1));
			newPos.y = (int)(Math.random() * (height + 1));
			count--;
		} while (isBlocked(newPos) && count > 0);
		
		return moveObject(name, startPos, newPos);
	}
	
	public boolean isBlocked(Point location) {
		for (Object obj: map[location.y][location.x]) {
			if (obj.getTouch() == Environment.Touch.HARD) {return true;}
		}
		return false;
	}
	
	public boolean isFood(Point location) {
		for (Object obj: map[location.y][location.x]) {
			if (obj.getTouch() == Environment.Touch.FOOD) {return true;}
		}
		return false;
	}
	
	public boolean isLegalPosition(Point pos) {
		if (pos.x < 0 || pos.y < 0 || pos.x >= width || pos.y >= height) {return false;}
		return true;
	}

	public List<Object>[][] getMap() {
		return map;
	}
	
	public List<Object> getTypedObjects(Environment.Touch type, Point position){
		ArrayList<Object> res = new ArrayList<Object>();
		
		for (Object obj: map[position.y][position.x]) {
			if (obj.getTouch() == type) {
				res.add(obj);
			}
		}
		return res;
	}

	public int getHeight() {return height;}

	public int getWidth() {return width;}

	public boolean isBigFood(Point location) {
		for (Object obj: map[location.y][location.x]) {
			if (obj.getTouch() == Environment.Touch.BIGFOOD) {return true;}
		}
		return false;
	}
	
	public boolean isClass(Point location, Class class_) {
		for (Object obj: map[location.y][location.x]) {
			if (obj.getClass() == class_) {return true;}
		}
		return false;
	}
	
	public Point getOrientedRelPos(Point startPos, Direction direction, Point relPos) {
		Point res = (Point) startPos.clone();
		
		try {
			switch(direction) {
				case NORTH:
					res.y += relPos.y;
					res.x += relPos.x;
					break;
				case SOUTH:
					res.y -= relPos.y;
					res.x -= relPos.x;
					break;
				case WEST:
					res.y -= relPos.x;
					res.x += relPos.y;
					break;
				case EAST:
					res.y += relPos.x;
					res.x -= relPos.y;
					break;
				default:
					throw new IllegalStateException("unknown direction: " + direction);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return (res);
	}
}

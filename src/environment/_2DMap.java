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
import objects.Fish;
import objects.OrientedFish;
import objects.Block;
import objects.Robot;
import robot.Action;

public class _2DMap {
	private List<Object>[][] map;
	private int height,width;
	private Environment env;
	private Map<String,Integer> object_number;
	private List<Robot> robot_list;
	
	@SuppressWarnings("unchecked")
	public _2DMap(int height_, int width_) {
		height = height_;
		width = width_;
		map = (ArrayList<environment.Object> [][]) new ArrayList[height][width];
		object_number = new HashMap<String,Integer>();
		robot_list = new ArrayList<Robot>();
	}
	
	@SuppressWarnings("unchecked")
	public _2DMap(Environment env_, String fileName) {
		object_number = new HashMap<String,Integer>();
		robot_list = new ArrayList<Robot>();
		env = env_;
				
		// reading and copying file
		List<String[]> file_copy = new ArrayList<String[]>();
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
		
		// filling map
		map = (ArrayList<Object> [][]) new ArrayList[height][width];	
		for (int l=0; l<height; l++) {
			for (int c=0; c<width; c++) {
				map[l][c]= new ArrayList<Object>();
				try{
					map[l][c].add(0, char_reader(file_copy.get(l)[c], new Point(c, l)));
				} catch (Exception e) {
					System.out.println("Error: unknown character was found and replaced with a wall: " + e.getMessage());
					object_number.compute("wall", (k,v) -> (v==null) ? 1 : v+1);
					map[l][c].add(new Block(this, Environment.Touch.HARD, Environment.WALL1,"wall", true, new Point(c, l)));
				}
			}
		}
	}
		
		
	public Object char_reader(String tile_type, Point position) throws Exception{
		
		switch (tile_type) {
			case "w":
				object_number.compute("wall", (k,v) -> (v==null) ? 1 : v+1);
				return new Block(this, Environment.Touch.HARD, Environment.WALL1,"wall", true, position);
			case "v":
				object_number.compute("robot", (k,v) -> (v==null) ? 1 : v+1);
				Robot rob_down = new Robot(this, Environment.ROBOT_COLOR, "robot.jpg", Environment.Touch.HARD, "robot_" + (object_number.get("robot") - 1), 
						true, Direction.SOUTH, position, Main.robot_vision_range);
				robot_list.add(rob_down);
				env.addAgent(rob_down);
				return rob_down;
			case ">":
				object_number.compute("robot", (k,v) -> (v==null) ? 1 : v+1);
				Robot rob_right = new Robot(this, Environment.ROBOT_COLOR, "robot.jpg", Environment.Touch.HARD, "robot_" + (object_number.get("robot") - 1), 
						true, Direction.EAST, position, Main.robot_vision_range);
				robot_list.add(rob_right);
				env.addAgent(rob_right);
				return rob_right;
			case "^":
				object_number.compute("robot", (k,v) -> (v==null) ? 1 : v+1);
				Robot rob_up = new Robot(this, Environment.ROBOT_COLOR, "robot.jpg", Environment.Touch.HARD, "robot_" + (object_number.get("robot") - 1),
						true, Direction.NORTH, position, Main.robot_vision_range);
				robot_list.add(rob_up);
				env.addAgent(rob_up);
				return rob_up;
			case "<":
				object_number.compute("robot", (k,v) -> (v==null) ? 1 : v+1);
				Robot rob_left = new Robot(this, Environment.ROBOT_COLOR, "robot.jpg", Environment.Touch.HARD, "robot_" + (object_number.get("robot") - 1), 
						true, Direction.WEST, position, Main.robot_vision_range);
				robot_list.add(rob_left);
				env.addAgent(rob_left);
				return rob_left;
				
			case "-":
				object_number.compute("empty", (k,v) -> (v==null) ? 1 : v+1);
				return new Block(this, Environment.Touch.EMPTY, Environment.FIELD_COLOR,"empty", false, position);
						
			case "*":
				object_number.compute("small_fish", (k,v) -> (v==null) ? 1 : v+1);
				return new Fish("small_fish.jpg", Environment.FISH1, Environment.Touch.FOOD, "small_fish"  + (object_number.get("small_fish") - 1)
						, true, Direction.NORTH, this, position, 1, Fish.on_death.RESPAWN_ELSEWHERE, Action.EAT);
				
			case "°":
				object_number.compute("big_fish", (k,v) -> (v==null) ? 1 : v+1);
				return new OrientedFish("big_fish" + (object_number.get("big_fish") - 1), true, Direction.NORTH, this, position, 
						OrientedFish.on_death.UNDYING, Action.FEAST);
				
			default:
				throw new IllegalStateException(tile_type);
		}
	}
	
	public boolean moveObject(String name, Point startPos, Point endPos) {
		if (!isLegalPosition(endPos)) {return false;}
		
		for (Object obj: map[startPos.y][startPos.x]) {
			if (obj.getName().equals(name)) {
				map[endPos.y][endPos.x].add(obj);
				map[startPos.y][startPos.x].remove(obj);
				obj.setPosition(endPos);
				return true;
			}
		}
		return false;
	}
	
	public boolean randomMove(String name, Point startPos) {
		Point newPos = new Point();
		int count = (int) Math.floor(width * height / 5);
		do {
			newPos.x = (int)(Math.random() * width);
			newPos.y = (int)(Math.random() * height);
			count--;
		} while ((isHard(newPos) || isFood(newPos))&& count > 0);
		
		return moveObject(name, startPos, newPos);
	}
	
	public boolean isHard(Point location) {
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
	
	public boolean isClass(Point location, Class<?> class_) {
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
		
		return res;
	}
	
	public Robot getRobot(int id) {return robot_list.get(id);}
	
	public void reset() {
		for (List<Object>[] line: map) {
			for (List<Object> location: line) {
				for (Object obj: location) {
					obj.reset();
				}
			}
		}
	}
	
	public int getNbRobot() {
		return robot_list.size();
	}
	
}

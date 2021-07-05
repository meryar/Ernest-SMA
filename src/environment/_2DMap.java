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
	private HashMap<Integer, Action> intentions;

	@SuppressWarnings("unchecked")
	public _2DMap(int height_, int width_) {
		height = height_;
		width = width_;
		map = (ArrayList<environment.Object> [][]) new ArrayList[height][width];
		object_number = new HashMap<String,Integer>();
		robot_list = new ArrayList<Robot>();
		intentions = new HashMap<Integer, Action>();
	}

	@SuppressWarnings("unchecked")
	public _2DMap(Environment env_, String fileName) {
		object_number = new HashMap<String,Integer>();
		robot_list = new ArrayList<Robot>();
		env = env_;
		intentions = new HashMap<Integer, Action>();

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
					OrientedFish.on_death.RESPAWN_ELSEWHERE, Action.FEAST);

		default:
			throw new IllegalStateException(tile_type);
		}
	}

	public boolean moveObject(String name, Point startPos, Point endPos) {
		if (!isLegalPosition(endPos)) {return false;}
		/*String[] names = {"big_fish0","big_fish1","big_fish2","big_fish3","big_fish4"};
		for (String name_: names) {
			if (name_.equals(name)) System.out.println(endPos);
		}*/
		
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

	public boolean randomIsolatedMove(String name, Point startPos) {
		Point newPos = new Point(-1,-1);
		Point pointL = new Point();
		Point pointR = new Point();
		Point pointS = new Point();
		Point pointN = new Point();
		int count = (int) Math.floor(width * height);
		do {
			newPos.x = (int)(Math.random() * width);
			newPos.y = (int)(Math.random() * height);
			pointL.x = newPos.x -1;
			pointL.y = newPos.y;
			pointR.x = newPos.x +1;
			pointR.y = newPos.y;
			pointS.x = newPos.x;
			pointS.y = newPos.y +1;
			pointS.x = newPos.x;
			pointS.y = newPos.y -1;
			count--;
		} while (count > 0 && ((isHard(newPos) || isFood(newPos)) ||
				(((!isLegalPosition(pointL) || isHard(pointL) || isFood(pointL))
				||(!isLegalPosition(pointR) || isHard(pointR) || isFood(pointR))) 
				&& ((!isLegalPosition(pointS) || isHard(pointS) || isFood(pointS))
				||(!isLegalPosition(pointN) || isHard(pointN) || isFood(pointN))))));
		
		//System.out.println(newPos + " hard: " + isHard(newPos) + " food: " + isFood(newPos));
		
		return moveObject(name, startPos, newPos);
	}

	public boolean randomMove(String name, Point startPos) {
		Point newPos = new Point(-1,-1);
		int count = (int) Math.floor(width * height);
		do {
			newPos.x = (int)(Math.random() * width);
			newPos.y = (int)(Math.random() * height);
			count--;
		} while ((isHard(newPos) || isFood(newPos))&& count > 0);

		return moveObject(name, startPos, newPos);
	}

	public boolean isHard(Point location) {
		for (Object obj: map[location.y][location.x]) {
			if (obj.getTouch() == Environment.Touch.HARD) {
				return true;
			}
			if (obj.getTouch() == Environment.Touch.HARD) {
				System.out.println("wtf?");
			}
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
	
	public void booking(int id, Action act) {
		intentions.put(id, act);
	}
	
	public void solve_conflicts() {
		Integer[][][] conflicts_map;
		HashMap<Integer, Point> targetPos = new HashMap<Integer, Point>();
		
		for (int id: intentions.keySet()) {
			Robot rob = getRobot(id);
			switch (intentions.get(id)) {
				case ROTATE_LEFT:  
					targetPos.put(id, rob.getPosition());
					break;
				case ROTATE_RIGHT:
					targetPos.put(id, rob.getPosition());
					break;
				case MOVE_FWD:
					Point newPos = getOrientedRelPos(rob.getPosition(), rob.getDirection(), new Point(0,-1));
					if (isLegalPosition(newPos) && !isHard(newPos) && !isFood(newPos)) { 
						targetPos.put(id, newPos);
					} else {
						targetPos.put(id, rob.getPosition());
						if (isFood(newPos)) {
							List<Object> targets = getTypedObjects(Environment.Touch.FOOD, newPos);
							for (Object fish: targets) {
								if (fish instanceof OrientedFish) {
									if (((OrientedFish) fish).get_attacked(rob.getName(), rob.getDirection())) {break;}
								} else if (fish instanceof Fish) {
									if (((Fish) fish).get_attacked(rob.getName())) {break;}
								}
							}	
						}
					}
					
					break;
				
				default:
			}
		}
		
		boolean conflict_found = true;
		
		while(conflict_found) {
			conflicts_map = new Integer[height][width][4];
			
			for (int id: targetPos.keySet()) {
				for (int i=0; i<4; i++) {
					if (conflicts_map[targetPos.get(id).y][targetPos.get(id).x][i] == null) {
						conflicts_map[targetPos.get(id).y][targetPos.get(id).x][i] = id;
						break;
					}
				}
			}
			
			conflict_found = false;
			for (Integer[][] line: conflicts_map) {
				for (Integer[] _case: line) {
					if (_case[1] != null) {
						conflict_found = true;
						int i = 0;
						while (i<4 && _case[i] !=null) {
							targetPos.put(_case[i], getRobot(_case[i]).getPosition());
							i++;
						}
					}
				}
			}
		}
		
		for (int id: intentions.keySet()) {
			Robot rob = getRobot(id);
			switch (intentions.get(id)) {
				case MOVE_FWD:
					Point newPos = getOrientedRelPos(getRobot(id).getPosition(), getRobot(id).getDirection(), new Point(0,-1));
					if (isLegalPosition(newPos)) {
						if (isHard(newPos) || (targetPos.get(id) == getRobot(id).getPosition() && !isFood(newPos))) {
							intentions.put(id, Action.BUMP);
						} else if (isFood(newPos)) {
							List<Object> targets = getTypedObjects(Environment.Touch.FOOD, newPos);
							for (Object fish: targets) {
								if (fish instanceof OrientedFish) { 
									if (((OrientedFish) fish).was_eaten_by(rob.getName(), rob.getDirection())) {
										intentions.put(id, ((OrientedFish) fish).getAffordedOnDeath());
									} else {
										intentions.put(id, Action.FIGHT);
									}
								} else if (fish instanceof Fish) {
									if (((Fish) fish).was_eaten_by(rob.getName())) {
										intentions.put(id, ((Fish) fish).getAffordedOnDeath());
									} else {
										intentions.put(id, Action.FIGHT);
									}
								}
							}
						}else {
							rob.move(newPos);
							intentions.put(id, Action.MOVE_FWD);
						}
					} 
					break;
				case ROTATE_LEFT:
					rob.setDirection(rob.rotatedDirection(rob.getDirection(), "left"));
					break;
				case ROTATE_RIGHT:
					rob.setDirection(rob.rotatedDirection(rob.getDirection(), "right"));
					break;
				default:
					System.err.println("UNEXPECTED COMMAND: " + intentions.get(id));
					break;
			}
		}
		
	}
	
	public Action check_results(int id) {return intentions.get(id);}
}

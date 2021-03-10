package environment;

public enum Direction {
	NORTH,
	WEST,
	SOUTH,
	EAST;
	
	public static int getID(Direction dir) {
		return (switch (dir){
		case EAST:
			yield 3;
		case NORTH:
			yield 0;
		case SOUTH:
			yield 2;
		case WEST:
			yield 1;
		default:
			yield -1;
		});
	}
	
	public static Direction getFromID(int id) {
		id += values().length; 
		id %= values().length; 
		return values()[id];
	}
}

package useful;

public class Pair<T,U>{
	public T member1;
	public U member2;
	
	public Pair(T m1, U m2){
		member1 = m1;
		member2 = m2;
	}

	public T left() { return member1;}
	public U right() { return member2;}
}

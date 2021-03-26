package robot;

import java.util.Vector;

public class InteractionSec {

	private InteractionPrim prim_inter;
	private Vector<Boolean> sec_inter;

	public InteractionSec(Action intended,int nbcapt) {
		prim_inter = new InteractionPrim(intended);
		sec_inter = new Vector<Boolean>(nbcapt);
	}

	public InteractionSec(InteractionPrim intended,int nbcapt) {
		prim_inter = intended;
		sec_inter = new Vector<Boolean>(nbcapt);
	}

	public InteractionPrim getPrimInter() {return prim_inter;}

	public Vector<Boolean> getSecInter() {return sec_inter;}

}

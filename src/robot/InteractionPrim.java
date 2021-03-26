package robot;

public class InteractionPrim {

	private Action intended_action;
	private Action enacted_action;

	public InteractionPrim(Action intended) {
		intended_action = intended;
	}

	public InteractionPrim(Action intended, Action enacted) {
		intended_action = intended;
		enacted_action = enacted;
	}

	public Action getIntendedAction() {return intended_action;}

	public Action getEnactedAction() {return enacted_action;}

	public void setEnactedAction(Action enacted) {enacted_action = enacted;}
}

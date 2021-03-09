package view;

import environment.Environment;

/**
 * This Abstract class is intended for windows displaying information regarding an agent or a robot 
 * and are supposed to be handled by the ControlView.
 * 
 * @author guedeta
 *
 */
public abstract class SlaveView extends AbstractView{

	private static final long serialVersionUID = 1L;

	public SlaveView(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Method used to set which entity the view should display the information of.
	 * 
	 * @param index: ID of the robot/agent to display the information of
	 * @param env: environement in wich the view will find the entity to focus on
	 */
	public abstract void setFocus(int ID, Environment env);
}

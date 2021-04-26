package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import agent_developpemental.FastNeuron;
import agents.AgentDeveloppemental;
import environment.Direction;
import environment.InterfaceAgentRobot;
import main.Main;
import robot.Action;

public class PrimSignPane extends JPanel{

	private static final long serialVersionUID = 1L;

	private final int between_screen_x = 5;
	private final int between_screen_y = 2;
	private final int reserved_offset = 30;

	private InterfaceAgentRobot agent;
	private FastNeuron[] primaries;
	private int selected;
	private int box_offset;
	private Vector<JLabel> labels;

	public PrimSignPane(int offset_y) {
		box_offset = offset_y;
		labels = new Vector<JLabel>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void paintComponent(Graphics g) {
		if (primaries != null) {
			int nb_colors = Main.colors.length + Direction.values().length - 1;
			int nb_sensors = agent.getRobot().getSensorNb();

			double sensor_map_side = Math.sqrt(agent.getRobot().getSensorNb());

			int sensor_map_height = (int) sensor_map_side;
			int sensor_map_width = (int) sensor_map_side;

			int nb_actions = Action.values().length;

			Dimension pane_size = getSize(); 

			g.setColor(Color.white);
			g.fillRect(0, 0, pane_size.width, pane_size.height);

			pane_size.height -= box_offset + reserved_offset;


			int screen_width = (int) pane_size.width / nb_actions - between_screen_x;
			int screen_height = (int) pane_size.height / 2 - between_screen_y;

			FastNeuron neuron = primaries[selected];

			for (int act=0; act<nb_actions; act++) {

				int screen_x = act * (screen_width + between_screen_x);

				for (int line=0; line<sensor_map_height; line++) {
					for (int column=0; column<sensor_map_height; column++) {
						int x = screen_x + (screen_width/sensor_map_width) * column;
						int y = box_offset + (screen_height/sensor_map_height) * line;

						g.setColor(new Color(
								normalize(act*nb_colors*nb_sensors + 0 * nb_sensors + line*sensor_map_width + column, neuron),
								normalize(act*nb_colors*nb_sensors + 1 * nb_sensors + line*sensor_map_width + column, neuron),
								normalize(act*nb_colors*nb_sensors + 2 * nb_sensors + line*sensor_map_width + column, neuron)));

						g.fillRect(x, y, screen_width/sensor_map_width, screen_height/sensor_map_height);

						g.setColor(new Color(
								normalize(act*nb_colors*nb_sensors + 6 * nb_sensors + line*sensor_map_width + column, neuron),
								normalize(act*nb_colors*nb_sensors + 4 * nb_sensors + line*sensor_map_width + column, neuron),
								normalize(act*nb_colors*nb_sensors + 5 * nb_sensors + line*sensor_map_width + column, neuron)));

						g.fillRect(x, y + screen_height + between_screen_y, screen_width/sensor_map_width, screen_height/sensor_map_height);
					}
				}

				if (labels.size() < nb_actions) {
					JLabel text = new JLabel("text", JLabel.CENTER);
					text.setVerticalAlignment(JLabel.CENTER);
					labels.add(text);
					add(text);
				}

				labels.get(act).setText("" + neuron.getWeights()[neuron.getWeights().length - Action.values().length + act]);
				labels.get(act).setBounds(act * (screen_width + between_screen_x), 
						pane_size.height + box_offset, 
						screen_width, 
						reserved_offset);

			}

			((JComboBox<Action>) getComponent(0)).setBounds((int)(pane_size.width/2.2), 5, 128, 22);
		}
	}

	private float normalize(int nb_index, FastNeuron neuron) {
		float nb = neuron.getWeights()[nb_index];
		float max = neuron.max_abs_weights[neuron.correspondingInteraction(nb_index)];
		if (max == 0) return 0.5f;

		return (float) Math.min((nb / max) / 2 + 0.5, 1f);
	}

	public void setAgent(InterfaceAgentRobot agent_) {
		agent = agent_;
		primaries = ((AgentDeveloppemental) agent.getAgent()).getPrimaries();
	}

	public void setFocus(int selectedIndex) {
		selected = selectedIndex;
	}

	public int getScreenHeight() {
		return (int) (getHeight() - box_offset - reserved_offset) / 2 - between_screen_y;
	}

	public int getYOffset() {
		return between_screen_y;
	}

	public int getScreenWidth() {
		return (int) getWidth() / Action.values().length - between_screen_x;
	}

	public int getXOffset() {
		return between_screen_x;
	}

	public Dimension getSensorsDimension() {
		double sensor_map_side = Math.sqrt(agent.getRobot().getSensorNb());

		int sensor_map_height = (int) sensor_map_side;
		int sensor_map_width = (int) sensor_map_side;

		return new Dimension(sensor_map_width, sensor_map_height);
	}
}

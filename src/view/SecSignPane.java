package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import agent_developpemental.Neuron;
import agents.AgentDeveloppemental;
import environment.Direction;
import environment.InterfaceAgentRobot;
import main.Main;
import robot.Action;

public class SecSignPane extends JPanel{

	private static final long serialVersionUID = 1L;

	private final int between_screen_x = 5;
	private final int between_screen_y = 2;
	private final int reserved_offset = 30;

	private InterfaceAgentRobot agent;
	private Neuron[] secondaries;
	private int interaction, color, sensor;
	private int box_offset;
	private Vector<JLabel> labels;

	public SecSignPane(int height_top_area) {

		Dimension dim = getSize();
		labels = new Vector<JLabel>();
		box_offset = height_top_area;

		JComboBox<Action> select = new JComboBox<Action>();
		select.setBounds(dim.width/3, 0, 95, height_top_area);  
		select.setAlignmentX(dim.width/3);
		for (Action act: Action.values()) {
			select.addItem(act);
		}
		select.addActionListener(
				new java.awt.event.ActionListener(){
					public void actionPerformed(ActionEvent e) {
						System.out.println("Now displaying signature of secondary interaction: color= "
								+ color 
								+ " sensor= " + sensor
								+ " interaction= " + select.getSelectedItem());
						setInter(select.getSelectedIndex());
						repaint();
					}
				});
		add(select); 


		JComboBox<Integer> selectColor = new JComboBox<Integer>();
		selectColor.setBounds(dim.width + 100, 0, 95 , height_top_area);
		select.setAlignmentX(dim.width/3 + 100);
		for (int i=0; i<Main.colors.length + Direction.values().length -1; i++) {
			selectColor.addItem(i);
		}
		selectColor.addActionListener(
				new java.awt.event.ActionListener(){
					public void actionPerformed(ActionEvent e) {
						System.out.println("Now displaying signature of secondary interaction: color= "
								+ selectColor.getSelectedItem()
								+ " sensor= " + sensor
								+ " interaction= " + select.getSelectedItem());
						setColor(selectColor.getSelectedIndex());
						repaint();
					}
				});
		add(new JLabel("color:"));
		add(selectColor); 

	}

	@Override
	public void paintComponent(Graphics g) {
		if (secondaries != null) {

			int nb_colors = Main.colors.length + Direction.values().length - 1;
			int nb_sensors = agent.getRobot().getSensorNb();
			int indexNeuron = interaction * (nb_colors * nb_sensors) + color * nb_sensors + sensor;

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

			Neuron neuron = secondaries[indexNeuron];

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
								normalize(act*nb_colors*nb_sensors + 3 * nb_sensors + line*sensor_map_width + column, neuron),
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

				labels.get(act).setText("" + neuron.getWeights().get(neuron.getWeights().size() - Action.values().length + act));
				labels.get(act).setBounds(act * (screen_width + between_screen_x), 
						pane_size.height + box_offset, 
						screen_width, 
						reserved_offset);

			}
		}
	}

	private float normalize(int nb_index, Neuron neuron) {
		float nb = neuron.getWeights().get(nb_index);
		float max = neuron.max_abs_weights[neuron.correspondingInteraction(nb_index)];

		return (float) Math.min((nb / max) / 2 + 0.5, 1f);
	}

	public void setAgent(InterfaceAgentRobot agent_) {
		agent = agent_;
		secondaries = ((AgentDeveloppemental) agent.getAgent()).getSecondaries();
	}

	public void setInter(int selectedIndex) {
		interaction = selectedIndex;
	}

	public void setColor(int colorIndex) {
		color = colorIndex;
	}

	public void setPositon(int sensorIndex) {
		sensor = sensorIndex;
	}


}

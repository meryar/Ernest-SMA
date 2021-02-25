package agent_developpemental;

import java.util.HashMap;
import java.util.Vector;

public class Data {
	
	private Vector<Float> data;
	private int offsetToInteraction, offsetToColor, offsetToPrim;
	private int nb_sensors, nb_colors, nb_interactions;
	
	public Data(int nbInteractions, int nbColors, int nbSensors) {
		nb_interactions = nbInteractions;
		nb_colors = nbColors;
		nb_sensors = nbSensors;
		
		data = new Vector<Float>(nb_interactions * nb_colors * nb_sensors + nb_interactions);
		data.setSize(nb_interactions * nb_colors * nb_sensors + nb_interactions);
		
		offsetToColor = nb_sensors;
		offsetToInteraction = nb_colors * offsetToColor;
		offsetToPrim = nb_interactions * offsetToInteraction;
		
	}
	
	public Data(int nbInteraction, int nbColors, int nbSensors, Vector<Float> data_) {
		this(nbInteraction, nbColors, nbSensors);
		assert (data_.size() == data.size()): "ERROR: given data's size is incorrect";
		data = data_;
	}
	
	public int getIndex(int interaction, int color, int sensor) {
		return interaction * offsetToInteraction 
				+ color * offsetToColor
				+ sensor;
	}
	
	public int getIndex(int interaction) {
		return offsetToPrim + interaction;
	}
	
	public float get(int index) {
		return data.get(index);
	}
	
	public float getSecondary(int interaction, int color, int sensor) {
		return data.get(getIndex(interaction, color, sensor));
	}
	
	public float getPrimary(int interaction) {
		return data.get(offsetToPrim + interaction);
	}
	
	public Vector<Float> getPrimarys() {
		Vector<Float> primarys = new Vector<Float>();
		for (int i=offsetToPrim; i<data.size(); i++) {
			primarys.add(data.get(i));
		}
		
		return primarys;
	}
	
	public int[] translate(int index){
		if (index > offsetToPrim - 1) {
			int[] res = {index - offsetToPrim, -1, -1};
			return res;
		} else {
			int[] res = {
					(int) (Math.floor(index / (nb_colors * nb_sensors))),
					(int) (Math.floor((index % (nb_colors * nb_sensors)) / nb_sensors)),
					index % nb_sensors
			};
			return res;
		}
	}
	
	public HashMap<Integer, Float> getEnactable(float enactability_treshold){
		isComplete();
		
		HashMap<Integer, Float> map = new HashMap<Integer, Float>();
		
		Vector<Integer> enactablePrim = new Vector<Integer>();
		Vector<Float> primaries = getPrimarys();
		for (int i=0; i<primaries.size(); i++) {
			int index = getIndex(i);
			map.put(index, get(index));
			if (primaries.get(i) >= enactability_treshold) {
				enactablePrim.add(i);
			}
		}
		
		for (int act: enactablePrim) {
			for (int c=0; c<nb_colors; c++) {
				for (int s=0; s<nb_sensors; s++) {
					int index = getIndex(act, c, s);
					map.put(index, get(index));
				}
			}
		}
		
		return map;
	}
	
	public boolean isComplete() {
		if (data.contains(null)) {
			System.err.println("Warning: data is incomplete!");
			return false;
		}
		return true;
	}

	// For testing purposes
	public static void main(String[] args){
		System.out.println("test");
		
		Data test = new Data(7, 4, 49);
		
		int[] translation = test.translate(1371);
		System.out.println(translation[0] + " " + translation[1] + " " + translation[2]);
	}
}


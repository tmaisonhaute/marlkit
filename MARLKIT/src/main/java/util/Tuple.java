package util;

import java.util.ArrayList;
import java.util.List;

public class Tuple {
	private List<Double> values;
	private int size;

	public Tuple(List<Double> values) {
		this.values = values;
		size = values.size();
	}
	
	public Double getValue(int index) {
		Double val = values.get(index);
		return val;
	}
	public int getSize() {
		return size;
	}
	
	public void setValue(int index, Double value) {
		this.values.set(index, value);
	}
	
	public Tuple add(Tuple t){
		if (t.getSize() != this.size) {
			throw new IllegalArgumentException("Tuple of differents size can't be added");
		}
		List<Double> newL = new ArrayList<Double>();
		for(int i = 0; i < this.getSize(); i ++) {
			Double v = this.getValue(i) + t.getValue(i);
			newL.set(i, v);
		}
		return new Tuple(newL);
	}
	public Tuple mul(double m){
		List<Double> newL = new ArrayList<Double>();
		for(int i = 0; i < this.getSize(); i ++) {
			Double v = this.getValue(i) * m;
			newL.set(i, v);
		}
		return new Tuple(newL);
	}
	
}

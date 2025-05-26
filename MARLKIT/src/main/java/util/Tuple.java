package util;

import java.util.ArrayList;
import java.util.List;

public class Tuple {
	private List<Double> values;

	public Tuple(List<Double> values) {
		this.values = values;
	}
	
	public Double getValue(int index) {
		return values.get(index);
	}
	public int getSize() {
		return values.size();
	}
	
	public void setValue(int index, Double value) {
		this.values.set(index, value);
	}
	
	public Tuple add(Tuple t){
		if (t.getSize() != this.getSize()) {
			throw new IllegalArgumentException("Tuple of differents size can't be added");
		}
		List<Double> newL = new ArrayList<>();
		for (int i = 0; i < this.getSize(); i++) {
			Double v = this.getValue(i) + t.getValue(i);
			newL.add(v);
		}
		return new Tuple(newL);
	}
	public Tuple multiply(double m){
		List<Double> newL = new ArrayList<>();
		for(int i = 0; i < this.getSize(); i ++) {
			Double v = this.getValue(i) * m;
			newL.add(v);
		}
		return new Tuple(newL);
	}
	public Tuple clone() {
		List<Double> newL = new ArrayList<>();
		for (int i = 0; i < this.getSize(); i++) {
			newL.add(this.getValue(i));
		}
		return new Tuple(newL);
	}
	
	@Override
    public String toString() {
    	return "" + String.join(", ", values.toString());
    }
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Tuple other) {
        	return values.equals(other.values);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }
	
}

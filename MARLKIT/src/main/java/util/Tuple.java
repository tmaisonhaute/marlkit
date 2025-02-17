package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
	public Tuple multiply(double m){
		List<Double> newL = new ArrayList<Double>();
		for(int i = 0; i < this.getSize(); i ++) {
			Double v = this.getValue(i) * m;
			newL.set(i, v);
		}
		return new Tuple(newL);
	}
	public Tuple clone() {
		List<Double> newL = new ArrayList<Double>();
		for (int i = 0; i < this.getSize(); i++) {
			newL.set(i, this.getValue(i));
		}
		return new Tuple(newL);
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple tuple = (Tuple) o;
        return size == tuple.size && Objects.equals(values, tuple.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, size);
    }
	
}

package util;

import java.util.ArrayList;
import java.util.List;

/**
 * A tuple of double values supporting arithmetic operations.
 */
public class Tuple {
	private List<Double> values;

	/**
	 * Creates a new tuple with the specified values.
	 *
	 * @param values the list of double values
	 */
	public Tuple(List<Double> values) {
		this.values = values;
	}
	
	/**
	 * Returns the value at the specified index.
	 *
	 * @param index the index
	 * @return the value at that index
	 */
	public Double getValue(int index) {
		return values.get(index);
	}
	
	/**
	 * Returns the size of this tuple.
	 *
	 * @return the number of elements
	 */
	public int getSize() {
		return values.size();
	}
	
	/**
	 * Sets the value at the specified index.
	 *
	 * @param index the index
	 * @param value the new value
	 */
	public void setValue(int index, Double value) {
		this.values.set(index, value);
	}
	
	/**
	 * Adds another tuple element-wise.
	 *
	 * @param t the tuple to add
	 * @return a new tuple with summed values
	 * @throws IllegalArgumentException if tuples have different sizes
	 */
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
	
	/**
	 * Multiplies all elements by a scalar.
	 *
	 * @param m the multiplier
	 * @return a new tuple with scaled values
	 */
	public Tuple multiply(double m){
		List<Double> newL = new ArrayList<>();
		for(int i = 0; i < this.getSize(); i ++) {
			Double v = this.getValue(i) * m;
			newL.add(v);
		}
		return new Tuple(newL);
	}
	
	/**
	 * Creates a copy of this tuple.
	 *
	 * @return a new tuple with the same values
	 */
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

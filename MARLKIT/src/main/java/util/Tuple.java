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
	 * Adds two tuples element-wise. Throws an exception if the tuples have different sizes.
	 * @param t1 the first tuple
	 * @param t2 the second tuple
	 * @return a new tuple with summed values
	 * @throws IllegalArgumentException if tuples have different sizes
	 */
	public static Tuple add(Tuple t1, Tuple t2) {
		if (t1.getSize() != t2.getSize()) {
			throw new IllegalArgumentException("Tuple of differents size can't be added");
		}
		List<Double> newL = new ArrayList<>();
		for (int i = 0; i < t1.getSize(); i++) {
			Double v = t1.getValue(i) + t2.getValue(i);
			newL.add(v);
		}
		return new Tuple(newL);
    }
	
	/**
	 * Adds another tuple element-wise.
	 *
	 * @param t the tuple to add
	 * @return a new tuple with summed values
	 * @throws IllegalArgumentException if tuples have different sizes
	 */
	public void add(Tuple t){
		if (t.getSize() != this.getSize()) {
			throw new IllegalArgumentException("Tuple of differents size can't be added");
		}
		for (int i = 0; i < this.getSize(); i++) {
			Double v = this.getValue(i) + t.getValue(i);
			this.setValue(i, v);
		}
	}
	
	/**
	 * Multiplies all elements of a tuple by a scalar.
	 * @param t the tuple to scale
	 * @param m the multiplier
	 * @return a new tuple with scaled values
	 */
	public static Tuple multiply(Tuple t, double m) {
		List<Double> newL = new ArrayList<>();
		for (int i = 0; i < t.getSize(); i++) {
			Double v = t.getValue(i) * m;
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
	public void multiply(double m){
		for(int i = 0; i < this.getSize(); i ++) {
			Double v = this.getValue(i) * m;
			this.setValue(i, v);
		}
	}
	
	/**
	 * Converts a Pair<Double, Double> to a Tuple.
	 * @param pair the pair to convert
	 * @return a new tuple containing the pair's values
	 */
	public static Tuple fromPair(Pair<Double, Double> pair) {
	    return new Tuple(List.of(pair.getFirst(), pair.getSecond()));
	}

	/**
	 * Converts this Tuple to a Pair<Double, Double>.
	 * The tuple must have exactly two elements; otherwise, an exception is thrown.
	 * @return a new pair containing the two values of this tuple
	 * @throws IllegalStateException if the tuple does not have exactly two elements
	 */
	public Pair<Double, Double> toPair2D() {
	    if (getSize() != 2) {
	        throw new IllegalStateException("Cannot convert Tuple of size " + getSize() + " to Pair<Double, Double>.");
	    }

	    return new Pair<>(getValue(0), getValue(1));
	}

	
	/**
	 * Creates a copy of this tuple.
	 *
	 * @return a new tuple with the same values
	 */
	public Tuple copy() {
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

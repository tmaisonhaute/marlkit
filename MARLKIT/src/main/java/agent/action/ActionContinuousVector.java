package agent.action;

import java.util.Arrays;

public class ActionContinuousVector implements Action {
	private double[] values;
	protected double lowerBound = Double.NEGATIVE_INFINITY;
	protected double upperBound = Double.POSITIVE_INFINITY;
	
	public ActionContinuousVector(double[] values) {
		this.values = Arrays.copyOf(values, values.length);
	}
	
	public ActionContinuousVector(double[] values, double lowerBound, double upperBound) {
		this.values = Arrays.copyOf(values, values.length);
		setBounds(lowerBound, upperBound);
	}
	
	public ActionContinuousVector(int size) {
		this.values = new double[size];
	}
	
	public void setBounds(double lowerBound, double upperBound) {
		if (lowerBound > upperBound) {
		    throw new IllegalArgumentException("lowerBound must be less than or equal to upperBound.");
		}
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		checkBounds();
	}
	
	/**
	 * Returns the lower bound for the action values.
	 * @return the lower bound
	 */
	public double getLowerBound() {
		return lowerBound;
	}

	/**
	 * Returns the upper bound for the action values.
	 * 
	 * @return the upper bound
	 */
	public double getUpperBound() {
		return upperBound;
	}
	
	public void clearBounds() {
		this.lowerBound = Double.NEGATIVE_INFINITY;
		this.upperBound = Double.POSITIVE_INFINITY;
	}
	
	/**
	 * Checks if the action values are within the specified bounds.
	 * If any value is out of bounds, it will be clipped to the nearest bound.
	 */
	protected void checkBounds() {
		for (int i = 0; i < values.length; i++) {
			checkBounds(i);
		}
	}
	
	/**
	 * Checks if the action value at the specified index is within the bounds.
	 * If the value is out of bounds, it will be clipped to the nearest bound.
	 * @param index the index of the value to check
	 */
	protected void checkBounds(int index) {
		if (Double.isNaN(values[index])) {
		    throw new IllegalArgumentException("Action values must not be NaN.");
		}
		if (values[index] < lowerBound) {
			values[index] = lowerBound;
		} else if (values[index] > upperBound) {
			values[index] = upperBound;
		}
	}
	
	public void setValue(int index, double value) {
		this.values[index] = value;
		checkBounds(index);
	}
	
	public double getValue(int index) {
		return this.values[index];
	}
	
	public void setValues(double[] values) {
		this.values = Arrays.copyOf(values, values.length);
		checkBounds();
	}
	
	public double[] getValues() {
		return Arrays.copyOf(values, values.length);
	}
	
	public int getSize() {
		return values.length;
	}
	

	@Override
	public ActionContinuousVector copy() {
	    ActionContinuousVector copy = new ActionContinuousVector(values);
	    copy.setBounds(lowerBound, upperBound);
	    return copy;
	}
	
	@Override
	public int hashCode() {
	    return Arrays.hashCode(values);
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) {
	        return true;
	    }
	    if (!(obj instanceof ActionContinuousVector other)) {
	        return false;
	    }
	    return Arrays.equals(values, other.values);
	}

}

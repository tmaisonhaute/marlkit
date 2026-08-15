package agent.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	
	/**
	 * Creates a new ActionContinuousVector by merging all individual actions in the given JointAction.
	 * @param jointAction the joint action containing individual ActionContinuousVector actions
	 * @return a new ActionContinuousVector containing all values from the individual actions
	 */
	public static ActionContinuousVector fromJointAction(JointAction jointAction) {
		if (jointAction.size() == 0) {
		    throw new IllegalArgumentException("Joint action must not be empty.");
		}
		
		List<Double> allValues = new ArrayList<>();
		double lowerBound = Double.POSITIVE_INFINITY;
		double upperBound = Double.NEGATIVE_INFINITY;
		
		for(Action action : jointAction.getActions()) {
            if (action instanceof ActionContinuousVector acv) {
                for (double value : acv.getValues()) {
                	allValues.add(value);
                }
                lowerBound = Math.min(lowerBound, acv.getLowerBound());
                upperBound = Math.max(upperBound, acv.getUpperBound());
            } else {
                throw new IllegalArgumentException("All actions in the joint action must be of type ActionContinuousVector.");
            }
        }
		
		double[] jointValues = allValues.stream().mapToDouble(Double::doubleValue).toArray();
		
		return new ActionContinuousVector(jointValues, lowerBound, upperBound);
	}

}

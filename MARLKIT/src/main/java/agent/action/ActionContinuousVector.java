package agent.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a continuous vector action.
 * 
 * <p>
 * This class encapsulates a vector of continuous values.
 * The values can be bounded within specified lower and upper limits.
 * </p>
 * 
 * 
 */
public class ActionContinuousVector implements Action {
	private double[] values;
	protected double lowerBound = Double.NEGATIVE_INFINITY;
	protected double upperBound = Double.POSITIVE_INFINITY;
	
	/**
	 * Creates a new ActionContinuousVector with the specified values.
	 * 
	 * <p>
	 * The bounds for the action values are set to negative and positive infinity by default.
	 * </p>
	 * 
	 * @param values the continuous values for the action
	 */
	public ActionContinuousVector(double[] values) {
		this.values = Arrays.copyOf(values, values.length);
	}
	
	/**
	 * Creates a new ActionContinuousVector with the specified values and bounds.
	 * @param values the continuous values for the action
	 * @param lowerBound the lower bound for the action values
	 * @param upperBound the upper bound for the action values
	 */
	public ActionContinuousVector(double[] values, double lowerBound, double upperBound) {
		this.values = Arrays.copyOf(values, values.length);
		setBounds(lowerBound, upperBound);
	}
	
	/**
	 * Creates a new ActionContinuousVector with the specified size.
	 * 
	 * <p>
	 * The values are initialized to zero, and the bounds are set to negative and positive infinity by default.
	 * </p>
	 * 
	 * @param size the size of the action vector
	 */
	public ActionContinuousVector(int size) {
		this.values = new double[size];
	}
	
	/**
	 * Sets the lower and upper bounds for the action values.
	 * 
	 * <p>
	 * Also checks if the current action values are within the specified bounds and clips them if necessary.
	 * </p>
	 * 
	 * @param lowerBound
	 * @param upperBound
	 */
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
	
	/**
	 * Clears the bounds for the action values, setting them to negative and positive infinity.
	 */
	public void clearBounds() {
		this.lowerBound = Double.NEGATIVE_INFINITY;
		this.upperBound = Double.POSITIVE_INFINITY;
	}
	
	/**
	 * Checks if the action values are within the specified bounds.
	 * If any value is out of bounds, it will be clipped to the nearest bound.
	 * @throws IllegalArgumentException if any value is NaN
	 */
	protected void checkBounds() {
		if (isThereNaN(this.values)) {
			throw new IllegalArgumentException("Action values must not be NaN.");
		}
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
	
	/**
	 * Sets the action value at the specified index.
	 * 
	 * <p>
	 * If the value is out of bounds, it will be clipped to the nearest bound.
	 * </p>
	 * 
	 * @param index the index of the value to set
	 * @param value the value to set
	 * @throws IllegalArgumentException if the value is NaN
	 */
	public void setValue(int index, double value) {
		if (Double.isNaN(value)) {
			throw new IllegalArgumentException("Action values must not be NaN.");
		}
		this.values[index] = value;
		checkBounds(index);
	}
	
	/**
	 * Returns the action value at the specified index.
	 * @param index the index of the value to retrieve
	 * @return the action value at the specified index
	 */
	public double getValue(int index) {
		return this.values[index];
	}
	
	/**
	 * Sets the action values for this ActionContinuousVector.
	 * @param values the continuous values to set
	 * @throws IllegalArgumentException if any value is NaN
	 */
	public void setValues(double[] values) {
		if (isThereNaN(values)) {
			throw new IllegalArgumentException("Action values must not be NaN.");
		}
		this.values = Arrays.copyOf(values, values.length);
		checkBounds();
	}
	
	/**
	 * Checks if any of the action values are NaN.
	 * 
	 * @param testedValues the array of values to test
	 * @return true if any value is NaN, false otherwise
	 */
	protected boolean isThereNaN(double[] testedValues) {
		for (int i = 0; i < testedValues.length; i++) {
			if (Double.isNaN(testedValues[i])) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns a copy of the action values for this ActionContinuousVector.
	 * @return a copy of the continuous values
	 */
	public double[] getValues() {
		return Arrays.copyOf(values, values.length);
	}
	
	/**
	 * Returns the size of the action vector.
	 * @return the number of continuous values in the action vector
	 */
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

	    if (values.length != other.values.length) {
	        return false;
	    }

	    for (int i = 0; i < values.length; i++) {
	        if (values[i] != other.values[i]) {
	            return false;
	        }
	    }

	    return true;
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

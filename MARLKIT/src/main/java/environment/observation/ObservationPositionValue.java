package environment.observation;
import java.util.Objects;

import util.Tuple;

/**
 * An observation representing a single position-value pair.
 * Used to observe specific locations in the environment with associated values.
 */
public class ObservationPositionValue implements Observation {
	protected Tuple position;
	protected double value;
	
	/**
	 * Creates a position-value observation.
	 *
	 * @param position the position tuple
	 * @param value the value at that position
	 */
	public ObservationPositionValue(Tuple position, double value) {
		this.position = position;
		this.value = value;
	}
	
	/**
	 * Returns the position of this observation.
	 *
	 * @return the position tuple
	 */
	public Tuple getPosition() {
		return position;
	}
	
	/**
	 * Sets the position of this observation.
	 *
	 * @param position the new position
	 */
	public void setPosition(Tuple position) {
		this.position = position;
	}
	
	/**
	 * Returns the value of this observation.
	 *
	 * @return the observation value
	 */
	public double getValue() {
		return value;
	}
	
	/**
	 * Sets the value of this observation.
	 *
	 * @param value the new value
	 */
	public void setValue(double value) {
		this.value = value;
	}
	
	/**
	 * Averages this observation with another position-value observation.
	 *
	 * @param other the observation to average with
	 * @return a new observation with averaged position and value
	 * @throws IllegalArgumentException if other is not an ObservationPositionValue
	 */
	@Override
	public void add(Observation other) {
	    if (!(other instanceof ObservationPositionValue)) {
	        throw new IllegalArgumentException("Impossible to add a ObservationPositionValue element with an element which isn't.");
	    }
	    
	    ObservationPositionValue otherOPV = (ObservationPositionValue) other;
	    

	    Tuple averagePosition = Tuple.multiply(Tuple.add(position, otherOPV.position),0.5);
	    double averageValue = (this.value + otherOPV.value) * 0.5;
	    this.position = averagePosition;
	    this.value = averageValue;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof ObservationPositionValue opv) {
        	return Double.compare(opv.value, value) == 0 && this.position.equals(opv.position);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, value);
    }
    
    @Override
    public String toString() {
    	return "(" + position.toString() + " -> " + value + ")";
    }
    
    @Override
	public ObservationPositionValue copy() {
		return new ObservationPositionValue(position.copy(), value);
	}
	
}

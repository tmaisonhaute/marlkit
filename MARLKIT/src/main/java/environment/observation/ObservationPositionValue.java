package environment.observation;
import java.util.Objects;

import util.Tuple;

public class ObservationPositionValue implements Observation {
	protected Tuple position;
	protected double value;
	
	public ObservationPositionValue(Tuple position, double value) {
		this.position = position;
		this.value = value;
	}
	public Tuple getPosition() {
		return position;
	}
	public void setPosition(Tuple position) {
		this.position = position;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	
	@Override
	public Observation add(Observation other) {
	    if (!(other instanceof ObservationPositionValue)) {
	        throw new IllegalArgumentException("Impossible to add a ObservationPositionValue element with an element which isn't.");
	    }
	    
	    ObservationPositionValue otherOPV = (ObservationPositionValue) other;
	    
	    // Fix: Use 0.5 explicitly instead of 1/2 which would result in integer division (0)
	    Tuple averagePosition = this.position.add(otherOPV.position).multiply(0.5);
	    double averageValue = (this.value + otherOPV.value) * 0.5;
	    
	    return new ObservationPositionValue(averagePosition, averageValue);
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
	
}

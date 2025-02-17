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
		if (other instanceof ObservationPositionValue) {
			ObservationPositionValue o = (ObservationPositionValue) other ;
			Tuple newPos = (this.position.add(o.position)).multiply(1/2);
			double newVal = (this.getValue() + o.getValue())/2;
			return new ObservationPositionValue(newPos, newVal);
		}else {
			throw new IllegalArgumentException("Impossible to add a ObservationPositionValue element with an element which isn't.");
		}
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObservationPositionValue that = (ObservationPositionValue) o;
        return Double.compare(that.value, value) == 0 && Objects.equals(position, that.position);
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

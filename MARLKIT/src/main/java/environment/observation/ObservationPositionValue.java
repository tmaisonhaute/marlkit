package environment.observation;
import util.*;

public class ObservationPositionValue implements Observation {
	private Tuple position;
	private double value;
	
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
			Tuple newPos = (this.position.add(o.position)).mul(1/2);
			double newVal = (this.getValue() + o.getValue())/2;
			return new ObservationPositionValue(newPos, newVal);
		}else {
			throw new IllegalArgumentException("Impossible to add a ObservationPositionValue element with an element which isn't.");
		}
	}
	
}

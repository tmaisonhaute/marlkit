package environment.observation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import util.Tuple;

/**
 * An observation composed of multiple position-value pairs.
 * Represents observations of multiple locations in the environment.
 */
public class ObservationPositionsValues implements Observation {

	protected List<ObservationPositionValue> obs;
	
	/**
	 * Creates an empty positions-values observation.
	 */
	public ObservationPositionsValues() {
		obs = new ArrayList<>();
	}
	
	/**
	 * Adds a position-value observation to this collection.
	 *
	 * @param o the observation to add
	 */
	public void addObservation(ObservationPositionValue o) {
		obs.add(o);
	}
	
	/**
	 * Returns the value at the specified index.
	 *
	 * @param index the index of the observation
	 * @return the value at that index
	 */
	public double getValue(int index) {
		return obs.get(index).getValue();
	}
	
	/**
	 * Returns the position at the specified index.
	 *
	 * @param index the index of the observation
	 * @return the position tuple at that index
	 */
	public Tuple getPosition(int index) {
		return obs.get(index).getPosition();
	}
	
	/**
	 * Returns the position-value observation at the specified index.
	 *
	 * @param index the index
	 * @return the observation at that index
	 */
	public ObservationPositionValue getObs(int index) {
		return obs.get(index);
	}
	
	/**
	 * Returns the list of all position-value observations.
	 *
	 * @return the list of observations
	 */
	public List<ObservationPositionValue> getListObs(){
		return obs;
	}
	
	/**
	 * Concatenates this observation with another, combining all position-value pairs.
	 *
	 * @param other the observation to concatenate
	 * @return a new observation containing all position-value pairs
	 * @throws IllegalArgumentException if other is not an ObservationPositionsValues
	 */
	@Override
	public Observation add(Observation other) {
		if (other instanceof ObservationPositionsValues o ) {
			ObservationPositionsValues newObs = new ObservationPositionsValues();
			for (ObservationPositionValue e : this.obs) {
				newObs.addObservationPosition(e);
			}
			for (ObservationPositionValue e : o.obs) {
				newObs.addObservationPosition(e);
			}
			return newObs;
		}else {
			throw new IllegalArgumentException("Impossible to add a ObservationPositionsValues element with an element which isn't.");
		}
	}
	
	/**
	 * Adds a position-value observation to this collection (alias for addObservation).
	 *
	 * @param o the observation to add
	 */
	public void addObservationPosition(ObservationPositionValue o) {
        obs.add(o);
    }
	
	@Override
	public String toString() {
		String s = "";
		for (ObservationPositionValue o : obs) {
			s += o.toString() + " ";
		}
		return s;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(obs);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
        if (obj instanceof ObservationPositionsValues opv) {
        	return getListObs().equals(opv.getListObs());
        }
        return false;
	}
	
	@Override
	public ObservationPositionsValues copy() {
		ObservationPositionsValues copy = new ObservationPositionsValues();
		for (ObservationPositionValue o : obs) {
			copy.addObservationPosition(o.copy());
		}
		return copy;
	}
	

}

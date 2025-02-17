package environment.observation;

import java.util.ArrayList;
import java.util.List;

import util.Tuple;

public class ObservationPositionsValues implements Observation {

	protected List<ObservationPositionValue> obs;
	
	public ObservationPositionsValues() {
		obs = new ArrayList<>();
	}
	
	public void addObservation(ObservationPositionValue o) {
		obs.add(o);
	}
	
	public double getValue(int index) {
		return obs.get(index).getValue();
	}
	public Tuple getPosition(int index) {
		return obs.get(index).getPosition();
	}
	public ObservationPositionValue getObs(int index) {
		return obs.get(index);
	}
	public List<ObservationPositionValue> getListObs(){
		return obs;
	}
	
	@Override
	public Observation add(Observation other) {
		if (other instanceof ObservationPositionsValues) {
			ObservationPositionsValues o = (ObservationPositionsValues) other;
			ObservationPositionsValues newObs = new ObservationPositionsValues();
			for (ObservationPositionValue e : this.obs) {
				newObs.add(e);
			}
			for (ObservationPositionValue e : o.obs) {
				newObs.add(e);
			}
			return newObs;
		}else {
			throw new IllegalArgumentException("Impossible to add a ObservationPositionsValues element with an element which isn't.");
		}
	}
	
	@Override
	public String toString() {
		String s = "";
		for (ObservationPositionValue o : obs) {
			s += o.toString() + " ";
		}
		return s;
	}

}

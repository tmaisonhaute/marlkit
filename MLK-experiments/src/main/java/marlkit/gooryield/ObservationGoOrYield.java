package marlkit.gooryield;

import java.util.Objects;

import environment.observation.Observation;

/**
 * Single-state observation for GoOrYield.
 */
public final class ObservationGoOrYield implements Observation {

	@Override
	public void add(Observation other) {
	}

	@Override
	public Observation copy() {
		return new ObservationGoOrYield();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ObservationGoOrYield;
	}

	@Override
	public int hashCode() {
		return Objects.hash("gooryield-observation");
	}

	@Override
	public String toString() {
		return "GoOrYieldObs";
	}
}

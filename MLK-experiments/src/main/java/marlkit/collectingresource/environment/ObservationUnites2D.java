package marlkit.collectingresource.environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import environment.observation.Observation;

/**
 * Observation for Collecting resource, exposing production units.
 */
public class ObservationUnites2D implements Observation {
	private final List<UniteProductionSpatial> unites;

	/**
	 * Create an observation for the Collecting resource state.
	 *
	 * @param unites Units available in the environment.
	 */
	public ObservationUnites2D(List<UniteProductionSpatial> unites) {
		this.unites = unites;
	}

	/**
	 * Collecting resource observations are not mergeable.
	 *
	 * @param other Observation to merge.
	 * @return Never returns normally.
	 */
	@Override
	public void add(Observation other) {
		if (other instanceof ObservationUnites2D) {
			throw new UnsupportedOperationException("Collecting resource observations do not support merging.");
		}
		throw new IllegalArgumentException("Impossible to add a ObservationUnites2D to another Observation");
	}

	/**
	 * Return the units referenced by this observation.
	 *
	 * @return Unit list.
	 */
	public List<UniteProductionSpatial> getUnites() {
		return unites;
	}

	/**
	 * Compute a hash of the observation content.
	 *
	 * @return Hash code.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(unites);
	}

	/**
	 * Compare observations for structural equality.
	 *
	 * @param obj Object to compare.
	 * @return True if equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ObservationUnites2D other) {
			return Objects.equals(unites, other.unites);
		}
		return false;
	}

	/**
	 * Create a copy of this observation.
	 *
	 * @return Copied observation.
	 */
	@Override
	public Observation copy() {
		return new ObservationUnites2D(new ArrayList<>(unites));
	}
}

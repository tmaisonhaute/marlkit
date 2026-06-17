package marlkit.collectingresource.environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import environment.observation.Observation;

/**
 * Observation for Trade2D, exposing production units.
 */
public class ObservationUnites2D implements Observation {
	private final List<UniteProductionSpatial> unites;

	/**
	 * Create an observation for the Trade2D state.
	 *
	 * @param unites Units available in the environment.
	 */
	public ObservationUnites2D(List<UniteProductionSpatial> unites) {
		this.unites = unites;
	}

	/**
	 * Trade2D observations are not mergeable.
	 *
	 * @param other Observation to merge.
	 * @return Never returns normally.
	 */
	@Override
	public Observation add(Observation other) {
		if (other instanceof ObservationUnites2D) {
			throw new UnsupportedOperationException("Trade2D observations do not support merging.");
		}
		throw new IllegalArgumentException("Impossible to add a ObservationUnites2D element with an element which isn't.");
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

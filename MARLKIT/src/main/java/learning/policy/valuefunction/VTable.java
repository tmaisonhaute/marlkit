package learning.policy.valuefunction;

import environment.observation.Observation;

/**
 * Tabular state value function V(s) for observations.
 * <p>
 * Maps observations (states) to their expected cumulative reward under the current policy.
 * Used in algorithms like Monte Carlo evaluation.
 * </p>
 *
 * @see ValueFunction
 */
public class VTable extends ValueFunction<Observation> {

	/**
	 * Creates a V-table with the specified default value for unseen states.
	 *
	 * @param defaultValue the default V-value for unvisited states
	 */
	public VTable(double defaultValue) {
		super(defaultValue);
	}

}

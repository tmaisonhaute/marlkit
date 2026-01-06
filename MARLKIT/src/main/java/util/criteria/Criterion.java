package util.criteria;

import java.util.Optional;

import environment.state.State;

/**
 * Interface for criteria that determine when certain conditions are met in the simulation.
 */
public interface Criterion {
	
	/**
	 * Updates the criterion based on the current state.
	 *
	 * @param state the optional current state
	 */
	public void update(Optional<State> state);
	
	/**
	 * Resets the criterion to its initial condition.
	 */
	public void reset();
	
	/**
	 * Checks if the criterion is currently met.
	 *
	 * @return true if the criterion is met, false otherwise
	 */
	public boolean isMet();
}

package util.criteria;

import java.util.Optional;

import environment.state.State;

/**
 * Abstract base class for criteria that track time through updates.
 * Maintains an internal counter that increments on each update.
 */
public abstract class TimeCounterCriterion implements Criterion {
    private int currentTime;
    
	/**
	 * Creates a new time counter criterion with time initialized to 0.
	 */
	protected TimeCounterCriterion() {
		this.currentTime = 0;
	}
	
	/**
     * Updates the criterion by incrementing the internal time counter.
     * This method should be called once per time step.
     * 
     * @param state The current environment state (not used in this implementation)
     */
	@Override
	public void update(Optional<State> state) {
		currentTime++;

	}

	/**
	 * Resets the internal time counter to zero.
	 */
	@Override
	public void reset() {
		currentTime = 0;

	}
	
	/**
     * @return The current time tracked by this criterion
     */
	public int getCurrentTime() {
        return currentTime;
    }

}

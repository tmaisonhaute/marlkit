package util.criteria;

/**
 * A criterion that is met when a specified time threshold has been reached or exceeded.
 * This class is useful for creating time-based conditions in simulations or environments,
 * such as ending episodes after a certain number of time steps.
 */
public class ReachTimeCriterion extends TimeCounterCriterion {
	private int threshold;
	
	/**
	 * Creates a new time-based criterion with the specified time threshold.
	 * 
	 * @param threshold The time threshold after which the criterion will be met
	 */
	public ReachTimeCriterion(int threshold) {
		super();
		this.threshold = threshold;
	}
	
	
	/**
	 * Checks if the criterion has been met.
	 * 
	 * @return true if the current time has reached or exceeded the threshold, false otherwise
	 */
	@Override
	public boolean isMet() {
		return getCurrentTime() >= threshold;
	}

	/**
	 * Gets the time threshold for this criterion.
	 * 
	 * @return The threshold value
	 */
	public int getThreshold() {
		return threshold;
	}

	/**
	 * Sets a new time threshold for this criterion.
	 * 
	 * @param threshold The new threshold value
	 */
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
}

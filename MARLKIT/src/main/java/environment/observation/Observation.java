package environment.observation;

/**
 * Represents an observation of the state environment.
 */
public interface Observation {
	/**
	 * Combines this observation with another observation.
	 *
	 * @param other the observation to add
	 * @return the combined observation
	 */
	Observation add(Observation other);
	
	/**
     * Implementations must override equals() to provide meaningful equality comparison.
     * This is required to properly compare observations.
     */
    @Override
    boolean equals(Object obj);
}

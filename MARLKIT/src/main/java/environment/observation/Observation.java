package environment.observation;

/**
 * Represents an observation in the environment. Observations can be combined with other observations to create a new observation.
 * Observation are also meant to be used as input to policies and critics.
 */
public interface Observation{
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
    
    public Observation copy();
}

package environment.observation;

/**
 * Represents an observation in the environment.
 */
public interface Observation {
	Observation add(Observation other);
	
	/**
     * Implementations must override equals() to provide meaningful equality comparison.
     * This is required to properly compare observations.
     */
    @Override
    boolean equals(Object obj);
}

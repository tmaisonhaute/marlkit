package environment.observation;

/**
 * Abstract base class for observations that provides default implementations.
 * Subclasses must implement the add and equals methods.
 */
public abstract class DefaultObservation implements Observation {

	/**
	 * Placeholder for adding observations. Must be overridden by subclasses.
	 *
	 * @param other the observation to add
	 * @return the combined observation (currently returns null)
	 */
	@Override
	public Observation add(Observation other) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		throw new UnsupportedOperationException("Equals must be implemented.");
	}

}

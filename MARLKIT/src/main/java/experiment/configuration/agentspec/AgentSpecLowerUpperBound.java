package experiment.configuration.agentspec;

public interface AgentSpecLowerUpperBound extends AgentSpec{

	/**
	 * Returns the lower bound for the action space.
	 * Default implementation returns Double.NEGATIVE_INFINITY, indicating no lower bound.
	 *
	 * @return the lower bound for the action space
	 */
	default double getLowerBound() {
		return Double.NEGATIVE_INFINITY;
	}

	/**
	 * Returns the upper bound for the action space.
	 * Default implementation returns Double.POSITIVE_INFINITY, indicating no upper bound.
	 *
	 * @return the upper bound for the action space
	 */
	default double getUpperBound() {
		return Double.POSITIVE_INFINITY;
	}

}

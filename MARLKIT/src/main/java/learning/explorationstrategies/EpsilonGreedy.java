package learning.explorationstrategies;

import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

import agent.action.Action;
import learning.ExplorationStrategy;

public abstract class EpsilonGreedy implements ExplorationStrategy {

	private int nbIterations;
	
	protected EpsilonGreedy() {
		this.nbIterations = 0;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns a random action with probability epsilon (exploration),
	 * or empty with probability (1 - epsilon) to signal exploitation.
	 * </p>
	 */
	@Override
	public Optional<Action> getExploratoryAction(List<Action> possibleActions, RandomGenerator prng) {
		if (prng.nextDouble() < getEpsilon()) {
			return Optional.of(possibleActions.get(prng.nextInt(possibleActions.size())));
		}
		return Optional.empty();
	}

	public abstract double getEpsilon();

	/**
	 * Increments the iteration counter, reducing future epsilon values if decay is enabled.
	 */
	@Override
	public void update() {
		nbIterations++;
	}
	
	/**
	 * Returns the number of iterations (updates) completed.
	 *
	 * @return the iteration count
	 */
	public int getNbIterations() {
		return nbIterations;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLoggerInfo() {
		return "EpsilonGreedy-epsilon=" + (getEpsilon()*100) + "%";
	}

}

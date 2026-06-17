package learning.explorationstrategies;

import learning.ExplorationStrategy;

/**
 * Epsilon-greedy exploration strategy.
 * <p>
 * With probability epsilon, selects a random action (exploration).
 * With probability (1 - epsilon), returns empty to signal exploitation.
 * Epsilon can optionally decay over time based on the number of updates.
 * </p>
 *
 * @see ExplorationStrategy
 */
public class EpsilonGreedyLinearDecay extends EpsilonGreedy {


	/**
	 * Creates an epsilon-greedy strategy with linear decay.
	 * <p>
	 * The effective epsilon is computed as: 1/nbIterations
	 * </p>
	 *
	 */
	public EpsilonGreedyLinearDecay() {
		super();
	}

	/**
	 * Computes the current epsilon value based on iterations.
	 *
	 * @return the current epsilon value
	 */
	public double getEpsilon() {
		return 1.0/getNbIterations();
	}

}

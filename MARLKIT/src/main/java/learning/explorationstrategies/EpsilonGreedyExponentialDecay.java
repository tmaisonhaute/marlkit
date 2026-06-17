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
public class EpsilonGreedyExponentialDecay extends EpsilonGreedy {

	private double epsilonBase;
	private double decayRate;

	/**
	 * Creates an epsilon-greedy strategy with exponential decay.
	 * <p>
	 * The effective epsilon is computed as: epsilonBase * decayRate^nbIterations
	 * </p>
	 *
	 * @param epsilonBase the initial epsilon value
	 * @param decayRate   the decay multiplier per iteration (e.g., 0.01 for 1% decay). Use 0.0 for no decay.
	 */
	public EpsilonGreedyExponentialDecay(double epsilonBase, double decayRate) {
		super();
		this.epsilonBase = epsilonBase;
		this.decayRate = Math.clamp(decayRate, 0.0, 1.0);
	}

	/**
	 * Creates an epsilon-greedy strategy without decay.
	 *
	 * @param epsilonBase the constant epsilon value
	 */
	public EpsilonGreedyExponentialDecay(double epsilonBase) {
		this(epsilonBase, 0.0);
	}

	/**
	 * Creates an epsilon-greedy strategy with default epsilon of 0.05.
	 */
	public EpsilonGreedyExponentialDecay() {
		this(0.05);
	}


	/**
	 * Computes the current epsilon value based on iterations.
	 *
	 * @return the current epsilon value
	 */
	public double getEpsilon() {
		return epsilonBase * Math.pow((1-decayRate), getNbIterations());
	}

	/**
	 * Sets the current epsilon by adjusting epsilonBase.
	 * <p>
	 * Computes the new base so that: newEpsilonBase * decayRate^nbIterations = epsilon
	 * </p>
	 *
	 * @param epsilon the desired current epsilon value
	 */
	public void setEpsilon(double epsilon) {
		if (decayRate == 0.0 || getNbIterations() == 0) {
			epsilonBase = epsilon;
		} else {
			epsilonBase = epsilon / Math.pow((1-decayRate), getNbIterations());
		}
	}

}

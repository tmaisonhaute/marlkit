package learning.policy.explorationsettings;

import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

import agent.action.Action;

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
public class EpsilonGreedy implements ExplorationStrategy {

	private double epsilonBase;
	private double decayRate;
	private int nbIterations;

	/**
	 * Creates an epsilon-greedy strategy with exponential decay.
	 * <p>
	 * The effective epsilon is computed as: epsilonBase * decayRate^nbIterations
	 * </p>
	 *
	 * @param epsilonBase the initial epsilon value
	 * @param decayRate   the decay multiplier per iteration (e.g., 0.01 for 1% decay). Use 0.0 for no decay.
	 */
	public EpsilonGreedy(double epsilonBase, double decayRate) {
		this.epsilonBase = epsilonBase;
		this.decayRate = Math.clamp(decayRate, 0.0, 1.0);
		this.nbIterations = 0;
	}

	/**
	 * Creates an epsilon-greedy strategy without decay.
	 *
	 * @param epsilonBase the constant epsilon value
	 */
	public EpsilonGreedy(double epsilonBase) {
		this(epsilonBase, 0.0);
	}

	/**
	 * Creates an epsilon-greedy strategy with default epsilon of 0.05.
	 */
	public EpsilonGreedy() {
		this(0.05);
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

	/**
	 * Computes the current epsilon value based on iterations.
	 *
	 * @return the current epsilon value
	 */
	public double getEpsilon() {
		return epsilonBase * Math.pow((1-decayRate), nbIterations);
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
		if (decayRate == 0.0 || nbIterations == 0) {
			epsilonBase = epsilon;
		} else {
			epsilonBase = epsilon / Math.pow((1-decayRate), nbIterations);
		}
	}

	/**
	 * Increments the iteration counter, reducing future epsilon values if decay is enabled.
	 */
	public void updateEpsilon() {
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
}

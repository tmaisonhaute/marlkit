package learning.policy;

/**
 * Abstract base class for policies that use epsilon-greedy exploration.
 * Epsilon decreases over time based on the number of iterations.
 */
public abstract class PolicyEpsilon implements Policy {

	protected double epsilonBase;
	protected double epsilonReduction;
	protected int nbIterations;
	
	/**
	 * Creates an epsilon-greedy policy with decay.
	 *
	 * @param epsilonBase the initial epsilon value
	 * @param epsilonDecrease the rate of epsilon decay
	 */
	protected PolicyEpsilon(double epsilonBase, double epsilonDecrease) {
        this.epsilonBase = epsilonBase;
        this.epsilonReduction = epsilonDecrease;
        this.nbIterations = 0;
	}
	
	/**
	 * Creates an epsilon-greedy policy without decay.
	 *
	 * @param epsilonBase the epsilon value
	 */
	protected PolicyEpsilon(double epsilonBase) {
        this(epsilonBase, 0.0);
	}
	
	/**
	 * Creates an epsilon-greedy policy with default epsilon of 0.05.
	 */
	protected PolicyEpsilon() {
        this(0.05);
    }
	
	/**
	 * Computes the current epsilon value based on iterations.
	 *
	 * @return the current epsilon value
	 */
	public double getEpsilon() {
		return epsilonBase / (1 + (nbIterations) * epsilonReduction);
	}

	/**
	 * Adjusts the base epsilon to achieve the desired current epsilon.
	 *
	 * @param epsilon the desired epsilon value
	 */
	public void setEpsilon(double epsilon) {
		double currentEpsilon = getEpsilon();
		if (currentEpsilon == 0) {
			epsilonBase = epsilon;
		}else {
			epsilonBase *= epsilon/currentEpsilon;
		}
	}

	/**
	 * Increments the iteration counter, affecting future epsilon values.
	 */
	public void updateEpsilon() {
		nbIterations++;
	}
	
	/**
	 * Returns the number of iterations completed.
	 *
	 * @return the iteration count
	 */
	public int getNbIterations() {
        return nbIterations;
    }
	
}

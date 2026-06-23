package marlkit.preyhunter.agent;

import agent.AgentStandard;
import learning.Algorithm;
import learning.Policy;
import learning.algorithms.NoLearningAlgorithm;
import marlkit.preyhunter.policies.PreyEscapePolicy;

/**
 * Prey agent for the PreyHunter environment.
 *
 * By default, the prey does not learn.
 * It uses a heuristic escape policy:
 * - if a hunter is visible, flee from the closest one;
 * - otherwise, move randomly.
 */
public class PreyAgent extends AgentStandard {

    private static final double DEFAULT_SPEED = 0.2;

    /**
     * Creates a prey agent with default heuristic policy.
     */
    public PreyAgent() {
        this(DEFAULT_SPEED);
    }
    
	public PreyAgent(double speed) {
		super();

		PreyEscapePolicy policy = new PreyEscapePolicy(speed);
		NoLearningAlgorithm algorithm = new NoLearningAlgorithm(policy);

		setPolicy(policy);
		setAlgorithm(algorithm);
	}

    /**
     * Creates a prey agent with custom policy and algorithm.
     *
     * @param policy policy
     * @param algorithm algorithm
     */
    public PreyAgent(Policy policy, Algorithm algorithm) {
        super(policy, algorithm);
    }
}
package marlkit.preyhunter.agent;

import agent.AgentStandard;
import learning.Algorithm;
import learning.Policy;

/**
 * Base class for hunter agents operating in the continuous PreyHunter
 * environment.
 *
 * <p>Concrete subclasses define the policy and learning algorithm used by the
 * hunter.</p>
 */
public class HunterAgent extends AgentStandard {

    /**
     * Creates a hunter agent without configuring its policy or algorithm.
     *
     * <p>Concrete subclasses must configure these components before the agent
     * is activated.</p>
     */
    protected HunterAgent() {
        super();
    }

    /**
     * Creates a hunter agent with the specified policy and algorithm.
     *
     * @param policy the policy used to select actions
     * @param algorithm the learning algorithm used to update the policy
     */
    public HunterAgent(Policy policy, Algorithm algorithm) {
        super(policy, algorithm);
    }
}
package experiment.configuration;

import java.util.Objects;

import learning.Algorithm;
import learning.Policy;

/**
 * Immutable pair of learning components assigned to an agent.
 * <p>
 * A {@code LearningComponents} object groups together the {@link Policy} used to
 * select actions and the {@link Algorithm} used to update the agent's behavior.
 * These two components are kept together because most learning algorithms require
 * a compatible policy type.
 * </p>
 * <p>
 * Instances of this class should generally be freshly created for each agent.
 * Reusing the same policy or algorithm instance across several agents may
 * unintentionally share learning state.
 * </p>
 */
public class LearningComponents {

    private final Policy policy;
    private final Algorithm algorithm;

    /**
     * Creates a pair of learning components.
     *
     * @param policy the policy used by the agent to select actions
     * @param algorithm the algorithm used by the agent to learn
     */
    public LearningComponents(Policy policy, Algorithm algorithm) {
        this.policy = Objects.requireNonNull(policy, "policy");
        this.algorithm = Objects.requireNonNull(algorithm, "algorithm");
    }

    /**
     * Returns the policy component.
     *
     * @return the policy
     */
    public Policy getPolicy() {
        return policy;
    }

    /**
     * Returns the algorithm component.
     *
     * @return the algorithm
     */
    public Algorithm getAlgorithm() {
        return algorithm;
    }
}
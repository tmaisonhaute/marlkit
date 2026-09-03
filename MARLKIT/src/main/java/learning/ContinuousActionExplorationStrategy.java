package learning;

import java.util.random.RandomGenerator;

import agent.action.ActionContinuousVector;

/**
 * Interface for exploration stategies that alter a continuous action produced by a policy.
 */
public interface ContinuousActionExplorationStrategy {

    /**
     * Applies exploration to a continuous action.
     *
     * @param action the deterministic action produced by the policy
     * @param prng the random generator
     * @return the exploratory action
     */
    ActionContinuousVector explore(ActionContinuousVector action, RandomGenerator prng);

    /**
     * Updates the internal state of the exploration strategy.
     */
    void update();

    /**
     * Returns information about the current exploration state.
     *
     * @return exploration information
     */
    String getLoggerInfo();
}

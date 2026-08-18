package learning.policies;

import agent.action.ActionContinuousVector;
import environment.observation.Observation;
import learning.ContinuousActionExplorationStrategy;
import learning.Policy;

/**
 * Defines a deterministic policy updated through gradients with respect to its
 * continuous action output.
 *
 * <p>The policy maps each input directly to a continuous action vector. Unlike
 * a categorical policy, it does not produce logits or sample actions from a
 * probability distribution.</p>
 */
public interface DeterministicPolicyGradient extends Policy, Parameterized {

    /**
     * Computes the continuous action produced by the policy for an input.
     *
     * @param input the observation
     * @return the continuous action produced by the policy
     */
    ActionContinuousVector forwardAction(Observation input);

    /**
     * Computes the continuous actions produced for a batch of inputs.
     *
     * @param inputs the observations
     * @return the continuous actions produced for each input
     */
    ActionContinuousVector[] forwardActions(Observation[] inputs);

    /**
     * Updates the policy from a loss gradient with respect to the continuous
     * action produced for one input.
     *
     * @param input the observation
     * @param dLossDAction the loss gradient with respect to each action component
     * @param learningRate the policy learning rate
     */
    void updateFromActionGradient(Observation input, double[] dLossDAction, double learningRate);

    /**
     * Updates the policy from loss gradients with respect to the continuous
     * actions produced for a batch of inputs.
     *
     * @param inputs the observations
     * @param dLossDActions the loss gradients with respect to the action vectors
     * @param learningRate the policy learning rate
     */
    void updateFromActionGradient(Observation[] inputs, double[][] dLossDActions, double learningRate);
    
    /**
     * Updates the internal state of the exploration strategy, if one is defined.
     */
    void updateExplorationStrategy();
    
    /**
     * Returns the exploration strategy used to explore the continuous action space.
     * @return the exploration strategy, or null if none is defined
     */
    public ContinuousActionExplorationStrategy getExplorationStrategy();

    /**
     * Sets the exploration strategy used to explore the continuous action space.
     * @param explorationStrategy
     */
    public void setExplorationStrategy(ContinuousActionExplorationStrategy explorationStrategy);
}
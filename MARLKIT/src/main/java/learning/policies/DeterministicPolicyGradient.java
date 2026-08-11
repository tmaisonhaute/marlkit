package learning.policies;

import agent.action.ActionContinuousVector;
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
     * @param input the policy input
     * @return the continuous action produced by the policy
     */
    ActionContinuousVector forwardAction(PolicyInput input);

    /**
     * Computes the continuous actions produced for a batch of inputs.
     *
     * @param inputs the policy inputs
     * @return the continuous actions produced for each input
     */
    ActionContinuousVector[] forwardActions(PolicyInput[] inputs);

    /**
     * Updates the policy from a loss gradient with respect to the continuous
     * action produced for one input.
     *
     * @param input the policy input
     * @param dLossDAction the loss gradient with respect to each action component
     * @param learningRate the policy learning rate
     */
    void updateFromActionGradient(PolicyInput input, double[] dLossDAction, double learningRate);

    /**
     * Updates the policy from loss gradients with respect to the continuous
     * actions produced for a batch of inputs.
     *
     * @param inputs the policy inputs
     * @param dLossDActions the loss gradients with respect to the action vectors
     * @param learningRate the policy learning rate
     */
    void updateFromActionGradient(PolicyInput[] inputs, double[][] dLossDActions, double learningRate);
}
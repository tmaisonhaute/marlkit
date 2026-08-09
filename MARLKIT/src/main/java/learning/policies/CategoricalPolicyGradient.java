package learning.policies;

import agent.action.Action;
import learning.Policy;
import util.VectorOperator;

/**
 * Policy interface for policy gradient methods, providing methods to compute action logits and 
 * update the policy network based on gradients.
 */
public interface CategoricalPolicyGradient extends Policy {

	/**
	* Computes the action logits for one policy input.
	*
	* @param input the policy input
	* @return one logit for each available action
	*/
	double[] forwardLogits(PolicyInput input);
	
	/**
	 * Computes raw action logits for a batch of inputs.
	 *
	 * @param inputs the policy inputs
	 * @return batch logits
	 */
    double[][] forwardLogits(PolicyInput[] inputs);

    /**
    * Updates the policy from a gradient with respect to the logits produced for
    * one input.
    *
    * @param input the policy input
    * @param dLossDLogits the loss gradient with respect to the logits
    * @param learningRate the learning rate
    */
    void updateFromLogitsGradient(PolicyInput input, double[] dLossDLogits, double learningRate);
    
    /**
	 * Updates the policy network from a batch of user-provided gradients w.r.t logits.
	 *
	 * @param inputs the policy inputs
	 * @param dLossDLogits dL/dLogits for each input
	 * @param learningRate the learning rate
	 */
    void updateFromLogitsGradient(PolicyInput[] inputs, double[][] dLossDLogits, double learningRate);
    
    /**
    * Computes the softmax probabilities associated with one logit vector.
    *
    * @param logits the action logits
    * @return the corresponding probability distribution
    */
    default double[] softmax(double[] logits) {
    	return VectorOperator.softmax(logits, getSoftmaxTemperature());
    }
    
    /**
     * Computes the softmax probabilities from the given logits using the policy's softmax temperature.
     * @param logits the raw action logits
     * @return the softmax probabilities
     */
    public default double[][] softmax(double[][] logits){
    	return VectorOperator.softmax(logits, getSoftmaxTemperature());
    }
    
    /**
     * Returns the softmax temperature used in the policy.
     * @return the softmax temperature
     */
    double getSoftmaxTemperature();

    /**
     * Returns the index of the given action in the action set.
     * @param action the action to find the index for
     * @return 	the index of the action in the action set
     */
    int actionIndex(Action action);
	
}

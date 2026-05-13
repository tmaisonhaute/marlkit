package learning.nn;

import java.util.List;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.wrapperobservationvector.WrapperPolicyInputVector;
import learning.policy.Policy;
import learning.policy.PolicyInput;

/**
 * Actor neural network for policy gradient methods.
 */
public class ActorNetwork implements Policy{

	
	private MLKAgent agent;
    private final NeuralNetwork network;
    private final WrapperPolicyInputVector inputWrapper;
    private final List<Action> actionSet;

    
    /**
	 * Creates an actor network for policy learning.
	 *
	 * @param inputSize the observation vector size
	 * @param hiddenSize the number of hidden neurons
	 * @param inputWrapper converts observations to vectors
	 * @param actionSet the set of possible actions
	 */
    public ActorNetwork(int inputSize, int hiddenSize,
                        WrapperPolicyInputVector inputWrapper,
                        List<Action> actionSet) {
        this.inputWrapper = inputWrapper;
        this.actionSet = actionSet;
        
        this.network = new NeuralNetwork(
        		new int[] {inputSize, hiddenSize, actionSet.size()},
        		NeuralNetwork.Activations.relu(),
        		NeuralNetwork.Activations.identity()
        );
    }
    
    @Override
    public void init(MLKAgent agent) {
    	this.agent = agent;
    	network.initializeParameters(agent.prng());
    	
    }
    
    @Override
    public MLKAgent getAgent() {
    	return agent;
    }
    
	@Override
	public RandomGenerator prng() {
		return agent.prng();
	}
    
    /**
	 * Selects an action based on the policy distribution for the given PolicyInput.
	 *
	 * @param input the current PolicyInput
	 * @return the selected action
	 */
	@Override
    public Action selectAction(PolicyInput input) {
        double[] logits = forwardLogits(input);
        double[] probs = softmax(logits);
        
        double cumulativeProbability = 0.0;
        double randomValue = prng().nextDouble();
		for (int i = 0; i < probs.length; i++) {
			cumulativeProbability += probs[i];
			if (randomValue < cumulativeProbability) {
				return actionSet.get(i);
			}
		}
        
        return actionSet.get(probs.length - 1); 
    }
    
    /**
     * Computes softmax probabilities for a batch of logits.
     * @param logitsBatch the batch of raw scores
     * @return batch of probability vectors
     */
	public double[][] softmax(double[][] logitsBatch) {
		double[][] out = new double[logitsBatch.length][];
		for (int i = 0; i < logitsBatch.length; i++) {
			out[i] = softmax(logitsBatch[i]);
		}
		return out;
	} 

	/**
	 * Computes a numerically stable softmax probability distribution from logits.
	 *
	 * @param logits the raw scores
	 * @return a probability vector summing to 1
	 * @throws NullPointerException if logits is null
	 * @throws IllegalArgumentException if logits is empty
	 */
	public double[] softmax(double[] logits) {
	    double max = maxValue(logits);
	    double[] shiftedExponentials = exponentiateShifted(logits, max);
	    double sum = sum(shiftedExponentials);
	    return normalizeOrUniform(shiftedExponentials, sum);
	}
	
	/**
	 * Computes maximum value in an array.
	 * 
	 * @param values the array of values
	 */
	private double maxValue(double[] values) {
	    double max = Double.NEGATIVE_INFINITY;
	    for (double v : values) {
	        max = Math.max(max, v);
	    }
	    return max;
	}
	
	/**
	 * Computes exponentials of shifted logits for numerical stability.
	 * @param logits the raw scores
	 * @param max the maximum logit value
	 * @return the exponentiated shifted logits
	 */
	private double[] exponentiateShifted(double[] logits, double max) {
	    double[] out = new double[logits.length];
	    for (int i = 0; i < logits.length; i++) {
	        out[i] = Math.exp(logits[i] - max);
	    }
	    return out;
	}
	
	/**
	 * Computes the sum of an array of values.
	 * @param values the array of values
	 * @return the sum
	 */
	private double sum(double[] values) {
	    double s = 0.0;
	    for (double v : values) {
	        s += v;
	    }
	    return s;
	}
	
	/**
	 * Normalizes values to sum to 1, or returns a uniform distribution if sum is
	 * invalid.
	 * 
	 * @param values the array of values
	 * @param sum    the sum of the values
	 * @return normalized probabilities or uniform distribution
	 */
	private double[] normalizeOrUniform(double[] values, double sum) {
	    if (sum == 0.0 || !Double.isFinite(sum)) {
	        return uniform(values.length);
	    }
	    return normalize(values, sum);
	}
	
	/**
	 * Normalizes values to sum to 1.
	 * 
	 * @param values the array of values
	 * @param sum    the sum of the values
	 * @return normalized probabilities
	 */
	private double[] normalize(double[] values, double sum) {
	    double[] out = new double[values.length];
	    for (int i = 0; i < values.length; i++) {
	        out[i] = values[i] / sum;
	    }
	    return out;
	}
	
	/**
	 * Generates a uniform probability distribution.
	 * @param size the size of the distribution
	 * @return the uniform probability vector
	 */
	private double[] uniform(int size) {
	    double[] out = new double[size];
	    double p = 1.0 / size;
	    for (int i = 0; i < size; i++) {
	        out[i] = p;
	    }
	    return out;
	}

	
	
	/**
	 * Computes raw action logits for a single input.
	 *
	 * @param input the policy input
	 * @return logits (one per action)
	 */
	public double[] forwardLogits(PolicyInput input) {
	    double[] inputVector = inputWrapper.transform(input);
	    return network.forward(inputVector);
	}

	/**
	 * Computes raw action logits for a batch of inputs.
	 *
	 * @param inputs the policy inputs
	 * @return batch logits
	 */
	public double[][] forwardLogits(PolicyInput[] inputs) {
	    double[][] out = new double[inputs.length][];
	    for (int i = 0; i < inputs.length; i++) {
	        out[i] = forwardLogits(inputs[i]);
	    }
	    return out;
	}

	/**
	 * Updates the policy network from a user-provided gradient w.r.t logits.
	 *
	 * @param input the policy input
	 * @param dLossDLogits dL/dLogits for this input
	 * @param learningRate the learning rate
	 */
	public void updateFromLogitsGradient(PolicyInput input, double[] dLossDLogits, double learningRate) {
	    double[] inputVector = inputWrapper.transform(input);
	    network.applyOutputGradient(inputVector, dLossDLogits, learningRate);
	}

	/**
	 * Updates the policy network from a batch of user-provided gradients w.r.t logits.
	 *
	 * @param inputs the policy inputs
	 * @param dLossDLogits dL/dLogits for each input
	 * @param learningRate the learning rate
	 */
	public void updateFromLogitsGradient(PolicyInput[] inputs, double[][] dLossDLogits, double learningRate) {
	    double[][] inputVectors = new double[inputs.length][];
	    for (int i = 0; i < inputs.length; i++) {
	        inputVectors[i] = inputWrapper.transform(inputs[i]);
	    }
	    network.applyOutputGradientBatch(inputVectors, dLossDLogits, learningRate);
	}

	public List<Action> getActionSet() {
		return actionSet;
	}

    
    
}

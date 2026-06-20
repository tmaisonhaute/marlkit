package learning.policies;

import java.util.List;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.wrapperobservationvector.WrapperPolicyInputVector;
import learning.nn.NeuralNetwork;
import util.VectorOperator;

/**
 * Actor neural network for policy gradient methods.
 */
public class ActorNetwork implements PolicyGradientPolicy{

	
	private MLKAgent agent;
    private final NeuralNetwork network;
    private final WrapperPolicyInputVector inputWrapper;
    private final List<Action> actionSet;
    protected double softmaxTemperature;

    
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
    	this(inputSize, hiddenSize, inputWrapper, actionSet, 1.0);
        
    }

	public ActorNetwork(int inputSize, int hiddenSize, WrapperPolicyInputVector inputWrapper, List<Action> actionSet,
			double softmaxTemperature) {
		
		this.inputWrapper = inputWrapper;
        this.actionSet = actionSet;
        
        this.network = new NeuralNetwork(
        		new int[] {inputSize, hiddenSize, actionSet.size()},
        		NeuralNetwork.Activations.relu(),
        		NeuralNetwork.Activations.identity()
        );
		this.softmaxTemperature = softmaxTemperature;
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
	 * Computes a numerically stable softmax probability distribution from logits.
	 *
	 * @param logits the raw scores
	 * @return a probability vector summing to 1
	 */
	public double[] softmax(double[] logits) {
	    return VectorOperator.softmax(logits, getSoftmaxTemperature());
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

	@Override
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

	@Override
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
	
	
	@Override
	public int actionIndex(Action action) {
	    int index = actionSet.indexOf(action);
	
	    if (index < 0) {
	        throw new IllegalArgumentException("Unknown action.");
	    }
	
	    return index;
	}

	@Override
	public double getSoftmaxTemperature() {
		return softmaxTemperature;
	}


    
    
}

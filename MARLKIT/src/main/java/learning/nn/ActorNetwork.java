package learning.nn;

import java.util.List;
import java.util.random.RandomGenerator;

import agent.action.Action;
import agent.action.wrapperactionvector.WrapperActionVector;
import environment.observation.Observation;
import environment.observation.wrapperobservationvector.WrapperObservationVector;

/**
 * Actor neural network for policy gradient methods.
 */
public class ActorNetwork {
    private NeuralNetwork network;
    private WrapperObservationVector observationWrapper;
    private WrapperActionVector actionWrapper;
    private List<Action> actionSet;
    private RandomGenerator pnrg;
    
    /**
	 * Creates an actor network for policy learning.
	 *
	 * @param inputSize the observation vector size
	 * @param hiddenSize the number of hidden neurons
	 * @param pnrg the random number generator
	 * @param learningRate the learning rate
	 * @param observationWrapper converts observations to vectors
	 * @param actionWrapper converts actions to/from vectors
	 * @param actionSet the set of possible actions
	 */
    public ActorNetwork(int inputSize, int hiddenSize, RandomGenerator pnrg, double learningRate, 
                        WrapperObservationVector observationWrapper,
                        WrapperActionVector actionWrapper,
                        List<Action> actionSet) {
        this.observationWrapper = observationWrapper;
        this.actionWrapper = actionWrapper;
        this.actionSet = actionSet;
        this.pnrg = pnrg;
        
        // Create neural network with observation input size and action output size
        this.network = new NeuralNetwork(
        	inputSize,
            hiddenSize,
            actionSet.size(),
            pnrg,
            learningRate
        );
    }
    
    /**
	 * Selects an action based on the policy distribution for the given observation.
	 *
	 * @param observation the current observation
	 * @return the selected action
	 */
    public Action selectAction(Observation observation) {
        double[] observationVector = observationWrapper.transform(observation);
        double[] actionPreferences = network.forward(observationVector);
        
        double[] probs = softmax(actionPreferences);
        
        double cumulativeProbability = 0.0;
        double randomValue = pnrg.nextDouble();
		for (int i = 0; i < probs.length; i++) {
			cumulativeProbability += probs[i];
			if (randomValue < cumulativeProbability) {
				return actionSet.get(i);
			}
		}
        
        return actionSet.get(probs.length - 1); 
    }
    
    /**
	 * Updates the actor network using the TD error.
	 *
	 * @param observation the observation where action was taken
	 * @param action the action that was taken
	 * @param tdError the temporal difference error from the critic
	 */
    public void update(Observation observation, Action action, double tdError) {
        double[] observationVector = observationWrapper.transform(observation);
        
        double[] currentOutput = network.forward(observationVector);
        
        double[] targetOutput = new double[actionSet.size()];
        System.arraycopy(currentOutput, 0, targetOutput, 0, currentOutput.length);
        
        int actionIndex = actionSet.indexOf(action);
        
        targetOutput[actionIndex] += tdError;
        
        network.update(observationVector, targetOutput);
    }
    
    public void setLearningRate(double learningRate) {
        network.setLearningRate(learningRate);
    }
    
    private double[] softmax(double[] logits) {
        double max = Double.NEGATIVE_INFINITY;
        for (double logit : logits) {
            max = Math.max(max, logit);
        }
        
        double sum = 0.0;
        double[] probs = new double[logits.length];
        for (int i = 0; i < logits.length; i++) {
            probs[i] = Math.exp(logits[i] - max);
            sum += probs[i];
        }
        
		if (sum == 0) {
			for (int i = 0; i < probs.length; i++) {
				probs[i] = 1.0 / probs.length;
			}
		}
		else {
			for (int i = 0; i < probs.length; i++) {
				probs[i] /= sum;
			}
		}
		return probs;
    }
}

package learning.nn;

import java.util.List;
import java.util.Random;

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
    private Random random;
    
    public ActorNetwork(int inputSize, int hiddenSize, double learningRate, 
                        WrapperObservationVector observationWrapper,
                        WrapperActionVector actionWrapper,
                        List<Action> actionSet) {
        this.observationWrapper = observationWrapper;
        this.actionWrapper = actionWrapper;
        this.actionSet = actionSet;
        this.random = new Random();
        
        // Create neural network with observation input size and action output size
        this.network = new NeuralNetwork(
        	inputSize,
            hiddenSize,
            actionSet.size(),
            learningRate
        );
    }
    
    public Action selectAction(Observation observation, double epsilon) {
        if (random.nextDouble() < epsilon) {
            // Exploration: random action
            return actionSet.get(random.nextInt(actionSet.size()));
        }
        
        // Exploitation: action with highest probability from network
        double[] observationVector = observationWrapper.transform(observation);
        double[] actionPreferences = network.forward(observationVector);
        
        double[] probs = softmax(actionPreferences);
        
        // Sample action based on probabilities
        double cumulativeProbability = 0.0;
        double randomValue = random.nextDouble();
		for (int i = 0; i < probs.length; i++) {
			cumulativeProbability += probs[i];
			if (randomValue < cumulativeProbability) {
				return actionSet.get(i);
			}
		}
        
        return actionSet.get(probs.length - 1); 
    }
    
    public void update(Observation observation, Action action, double tdError) {
        double[] observationVector = observationWrapper.transform(observation);
        
        // Forward pass to get current action preferences
        double[] currentOutput = network.forward(observationVector);
        
        // Create target output based on current output
        double[] targetOutput = new double[actionSet.size()];
        System.arraycopy(currentOutput, 0, targetOutput, 0, currentOutput.length);
        
        int actionIndex = actionSet.indexOf(action);
        
        // Add the TD error to the selected action's preference
        // This increases its probability when TD error is positive
        targetOutput[actionIndex] += tdError;
        
        // Update the network toward these new target values
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
			// Avoid division by zero
			for (int i = 0; i < probs.length; i++) {
				probs[i] = 1.0 / probs.length; // Uniform distribution
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

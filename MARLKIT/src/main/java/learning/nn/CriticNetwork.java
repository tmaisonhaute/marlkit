package learning.nn;

import java.util.random.RandomGenerator;

import environment.observation.Observation;
import environment.observation.wrapperobservationvector.WrapperPolicyInputVector;

/**
 * Critic neural network for value function approximation.
 */
public class CriticNetwork {
    private NeuralNetwork1 network;
    private WrapperPolicyInputVector observationWrapper;
    
    /**
	 * Creates a critic network for value function approximation.
	 *
	 * @param inputSize the observation vector size
	 * @param hiddenSize the number of hidden neurons
	 * @param pnrg the random number generator
	 * @param learningRate the learning rate
	 * @param observationWrapper converts observations to vectors
	 */
    public CriticNetwork(int inputSize, int hiddenSize, RandomGenerator pnrg, double learningRate, 
                         WrapperPolicyInputVector observationWrapper) {
        this.observationWrapper = observationWrapper;
        
        // Create neural network with observation input size and single output for value
        this.network = new NeuralNetwork1(
        	inputSize,
            hiddenSize,
            1,
            pnrg,
            learningRate
        );
    }
    
    /**
	 * Estimates the value of the given observation.
	 *
	 * @param observation the observation to evaluate
	 * @return the estimated value
	 */
    public double getValue(Observation observation) {
        double[] observationVector = observationWrapper.transform(observation);
        double[] output = network.forward(observationVector);
        return output[0]; // Single output value
    }
    
    /**
	 * Updates the critic network towards the target value.
	 *
	 * @param observation the observation to update
	 * @param targetValue the target value
	 * @return the temporal difference error
	 */
    public double update(Observation observation, double targetValue) {
        double[] observationVector = observationWrapper.transform(observation);
        double currentValue = getValue(observation);
        double tdError = targetValue - currentValue;
        
        double[] target = new double[1];
        target[0] = targetValue;
        
        network.update(observationVector, target);
        
        return tdError;
    }
    
    public void setLearningRate(double learningRate) {
        network.setLearningRate(learningRate);
    }
}

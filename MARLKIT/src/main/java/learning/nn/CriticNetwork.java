package learning.nn;

import environment.observation.Observation;
import environment.observation.wrapperobservationvector.WrapperObservationVector;

/**
 * Critic neural network for value function approximation.
 */
public class CriticNetwork {
    private NeuralNetwork network;
    private WrapperObservationVector observationWrapper;
    
    public CriticNetwork(int inputSize, int hiddenSize, double learningRate, 
                         WrapperObservationVector observationWrapper) {
        this.observationWrapper = observationWrapper;
        
        // Create neural network with observation input size and single output for value
        this.network = new NeuralNetwork(
        	inputSize,
            hiddenSize,
            1,  // Output size is 1 for the value function
            learningRate
        );
    }
    
    public double getValue(Observation observation) {
        double[] observationVector = observationWrapper.transform(observation);
        double[] output = network.forward(observationVector);
//        System.out.println("CriticNetwork.getValue: " + output[0]);
        return output[0]; // Single output value
    }
    
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

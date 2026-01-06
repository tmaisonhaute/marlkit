package learning.nn;

import java.util.random.RandomGenerator;

/**
 * Simple feedforward neural network with one hidden layer.
 */
public class NeuralNetwork {
    private double[][] weightsInputToHidden;
    private double[] biasesHidden;
    private double[][] weightsHiddenToOutput;
    private double[] biasesOutput;
    
    private int inputSize;
    private int hiddenSize;
    private int outputSize;
    
    private double learningRate;
    private boolean useBiases;
    private RandomGenerator pnrg;
    
	/**
	 * Creates a neural network without biases.
	 *
	 * @param inputSize the number of input neurons
	 * @param hiddenSize the number of hidden layer neurons
	 * @param outputSize the number of output neurons
	 * @param pnrg the random number generator for weight initialization
	 * @param learningRate the learning rate for gradient descent
	 */
	public NeuralNetwork(int inputSize, int hiddenSize, int outputSize, RandomGenerator pnrg, double learningRate) {
		this(inputSize, hiddenSize, outputSize, pnrg, learningRate, false);
	}
    
    /**
	 * Creates a neural network with optional biases.
	 *
	 * @param inputSize the number of input neurons
	 * @param hiddenSize the number of hidden layer neurons
	 * @param outputSize the number of output neurons
	 * @param pnrg the random number generator for weight initialization
	 * @param learningRate the learning rate for gradient descent
	 * @param useBiases whether to use bias terms
	 */
    public NeuralNetwork(int inputSize, int hiddenSize, int outputSize, RandomGenerator pnrg, double learningRate, boolean useBiases) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.outputSize = outputSize;
        this.learningRate = learningRate;
        this.useBiases = useBiases;
        this.pnrg = pnrg;
        
        // Initialize weights with small random values
        weightsInputToHidden = new double[inputSize][hiddenSize];
        weightsHiddenToOutput = new double[hiddenSize][outputSize];
        
	    // Initialize biases only if using them
	    if (useBiases) {
	    	biasesHidden = new double[hiddenSize];
        	biasesOutput = new double[outputSize];
        } else {
            // Set to zero-length arrays or null
            biasesHidden = new double[0];
            biasesOutput = new double[0];
        }
        
        initializeWeights();
    }
    
    private void initializeWeights() {
        // Xavier initialization 
        double xavierInputToHidden = Math.sqrt(6.0 / (inputSize + hiddenSize));
        double xavierHiddenToOutput = Math.sqrt(6.0 / (hiddenSize + outputSize));
        
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                weightsInputToHidden[i][j] = (pnrg.nextDouble() * 2 - 1) * xavierInputToHidden;
            }
        }
        
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                weightsHiddenToOutput[i][j] = (pnrg.nextDouble() * 2 - 1) * xavierHiddenToOutput;
            }
        }
        
		if (useBiases) {
			for (int i = 0; i < hiddenSize; i++) {
                biasesHidden[i] = (pnrg.nextDouble() * 2 - 1) * xavierInputToHidden;
			}
			for (int i = 0; i < outputSize; i++) {
				biasesOutput[i] = (pnrg.nextDouble() * 2 - 1) * xavierHiddenToOutput;
			}	
		}
        
    }
    
    public double[] forward(double[] input) {
        if (input.length != inputSize) {
            throw new IllegalArgumentException("Input size must be " + inputSize + " but got " + input.length);
        }
        
        // Calculate hidden layer activations (using ReLU)
        double[] hiddenActivations = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            double sum = useBiases? biasesHidden[i] : 0;
            for (int j = 0; j < inputSize; j++) {
                sum += input[j] * weightsInputToHidden[j][i];
            }
            hiddenActivations[i] = relu(sum);
        }
        
        // Calculate output layer activations (linear for critic, softmax for actor)
        double[] outputActivations = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            double sum = useBiases ? biasesOutput[i] : 0;
            for (int j = 0; j < hiddenSize; j++) {
                sum += hiddenActivations[j] * weightsHiddenToOutput[j][i];
            }
            outputActivations[i] = sum;
        }
        
        return outputActivations;
    }
    
    public void backward(double[] input, double[] hiddenActivations, double[] output, double[] targetOutput) {
        // Calculate output layer errors
        double[] outputErrors = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            outputErrors[i] = targetOutput[i] - output[i];
        }
        
        // Update output layer weights and biases
        for (int i = 0; i < hiddenSize; i++) {
            for (int j = 0; j < outputSize; j++) {
                weightsHiddenToOutput[i][j] += learningRate * outputErrors[j] * hiddenActivations[i];
            }
        }
        
		if (useBiases) {
			for (int i = 0; i < outputSize; i++) {
				biasesOutput[i] += learningRate * outputErrors[i];
			}
		}
        
        // Calculate hidden layer errors
        double[] hiddenErrors = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            double error = 0;
            for (int j = 0; j < outputSize; j++) {
                error += outputErrors[j] * weightsHiddenToOutput[i][j];
            }
            hiddenErrors[i] = error * reluDerivative(hiddenActivations[i]);
        }
        
        // Update hidden layer weights and biases
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                weightsInputToHidden[i][j] += learningRate * hiddenErrors[j] * input[i];
            }
        }
        if (useBiases) {
	        for (int i = 0; i < hiddenSize; i++) {
	            biasesHidden[i] += learningRate * hiddenErrors[i];
	        }
        }
    }
    
    public void update(double[] input, double[] targetOutput) {
        // Forward pass
        double[] hiddenActivations = new double[hiddenSize];
        for (int i = 0; i < hiddenSize; i++) {
            double sum = useBiases ? biasesHidden[i] : 0;
            for (int j = 0; j < inputSize; j++) {
                sum += input[j] * weightsInputToHidden[j][i];
            }
            hiddenActivations[i] = relu(sum);
        }
        
        double[] output = new double[outputSize];
        for (int i = 0; i < outputSize; i++) {
            double sum = useBiases ? biasesOutput[i] : 0;
            for (int j = 0; j < hiddenSize; j++) {
                sum += hiddenActivations[j] * weightsHiddenToOutput[j][i];
            }
            output[i] = sum;
        }
        
        // Backward pass
        backward(input, hiddenActivations, output, targetOutput);
    }
    
    private double relu(double x) {
        return Math.max(0, x);
    }
    
    private double reluDerivative(double x) {
        return x > 0 ? 1 : 0;
    }
    
    public int getInputSize() {
        return inputSize;
    }
    
    public int getOutputSize() {
        return outputSize;
    }
    
    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }
}

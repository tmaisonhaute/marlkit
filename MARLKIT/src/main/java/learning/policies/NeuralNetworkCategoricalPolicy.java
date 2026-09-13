package learning.policies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import environment.observation.wrapperobservationvector.WrapperObservationVector;
import learning.nn.NeuralNetwork;
import util.VectorOperator;

/**
 * Neural categorical policy for discrete action spaces.
 *
 * The network outputs one logit per action.
 * Actions are sampled from the softmax distribution over logits.
 */
public class NeuralNetworkCategoricalPolicy implements CategoricalPolicyGradient, Parameterized {

    private final List<Action> actions;
    private final WrapperObservationVector inputWrapper;
    private final NeuralNetwork network;
    private final double softmaxTemperature;

    private MLKAgent agent;

    public NeuralNetworkCategoricalPolicy(
            List<Action> actions,
            WrapperObservationVector inputWrapper,
            int inputSize,
            int[] hiddenLayers,
            double softmaxTemperature
    ) {
        if (actions == null || actions.isEmpty()) {
            throw new IllegalArgumentException("Actions must not be null or empty.");
        }

        if (inputSize <= 0) {
            throw new IllegalArgumentException("Input size must be strictly positive.");
        }
        if (softmaxTemperature <= 0.0) {
        	throw new IllegalArgumentException("softmaxTemperature must be > 0.");
        }

        this.actions = new ArrayList<>(actions);
        this.inputWrapper = inputWrapper;
        this.softmaxTemperature = softmaxTemperature;

        int[] architecture = buildArchitecture(inputSize, hiddenLayers, actions.size());
        this.network = NeuralNetwork.reluIdentity(architecture);
    }

    @Override
    public void init(MLKAgent agent) {
        this.agent = agent;
        this.network.initializeParameters(agent.prng());
    }

    @Override
    public MLKAgent getAgent() {
        return agent;
    }

    @Override
    public Action selectAction(Observation input) {
        double[] logits = forwardLogits(input);
        double[] probabilities = VectorOperator.softmax(logits, softmaxTemperature);
        int actionIndex = sample(probabilities, prng());
        return actions.get(actionIndex).copy();
    }
    
    @Override
    public double[] forwardLogits(Observation input) {
    	double[] vector = inputWrapper.transform(input);
    	return network.forward(vector);
    }

    @Override
    public double[][] forwardLogits(Observation[] inputs) {
        double[][] logits = new double[inputs.length][];

        for (int i = 0; i < inputs.length; i++) {
            logits[i] = forwardLogits(inputs[i]);
        }

        return logits;
    }
    
    @Override
    public void updateFromLogitsGradient(Observation input, double[] dLossDLogits, double learningRate) {
        double[] vector = inputWrapper.transform(input);
        network.applyOutputGradient(vector, dLossDLogits, learningRate);
    }

    @Override
    public void updateFromLogitsGradient(
    		Observation[] inputs,
            double[][] dLossDLogits,
            double learningRate
    ) {
        double[][] vectors = new double[inputs.length][];

        for (int i = 0; i < inputs.length; i++) {
            vectors[i] = inputWrapper.transform(inputs[i]);
        }

        network.applyOutputGradientBatch(vectors, dLossDLogits, learningRate);
    }

    @Override
    public double getSoftmaxTemperature() {
        return softmaxTemperature;
    }

    @Override
    public int actionIndex(Action action) {
        for (int i = 0; i < actions.size(); i++) {
            if (actions.get(i).equals(action)) {
                return i;
            }
        }

        throw new IllegalArgumentException("Unknown action: " + action);
    }

    public List<Action> getActions() {
        List<Action> copies = new ArrayList<>();

        for (Action action : actions) {
            copies.add(action.copy());
        }

        return copies;
    }

    public double probability(Observation input, Action action) {
        double[] logits = forwardLogits(input);
        double[] probabilities = VectorOperator.softmax(logits, softmaxTemperature);
        return probabilities[actionIndex(action)];
    }

    public double logProbability(Observation input, Action action) {
        return Math.log(probability(input, action) + 1e-12);
    }

    /**
     * Builds the architecture of the neural network given the input size, hidden layers, and output size.
     * @param inputSize 
     * @param hiddenLayers
     * @param outputSize
     * @return an array representing the architecture of the neural network
     */
    private int[] buildArchitecture(int inputSize, int[] hiddenLayers, int outputSize) {
        int hiddenCount = hiddenLayers == null ? 0 : hiddenLayers.length;
        int[] architecture = new int[hiddenCount + 2];

        architecture[0] = inputSize;

        for (int i = 0; i < hiddenCount; i++) {
            architecture[i + 1] = hiddenLayers[i];
        }

        architecture[architecture.length - 1] = outputSize;

        return architecture;
    }

    /**
     * Samples an index from a categorical distribution defined by the given probabilities.
     * @param probabilities the probabilities for each category (should sum to 1)
     * @param random the random number generator
     * @return the index of the sampled category
     */
    private int sample(double[] probabilities, RandomGenerator random) {
        double r = random.nextDouble();
        double cumulative = 0.0;

        for (int i = 0; i < probabilities.length; i++) {
            cumulative += probabilities[i];

            if (r <= cumulative) {
                return i;
            }
        }

        return probabilities.length - 1;
    }

    @Override
    public double[] getParameters() {
        return network.getParameters();
    }

    @Override
    public void setParameters(double[] parameters) {
        network.setParameters(parameters);
    }

    @Override
    public String toString() {
        return "NeuralCategoricalPolicy{" +
                "actions=" + actions.size() +
                ", softmaxTemperature=" + softmaxTemperature +
                ", architecture=" + Arrays.toString(network.layerSizes()) +
                '}';
    }
}
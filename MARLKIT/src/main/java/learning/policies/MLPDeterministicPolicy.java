package learning.policies;

import java.util.Arrays;
import java.util.Objects;

import agent.MLKAgent;
import agent.action.ActionContinuousVector;
import environment.observation.wrapperobservationvector.WrapperPolicyInputVector;
import learning.ContinuousActionExplorationStrategy;
import learning.nn.NeuralNetwork;

/**
 * Neural deterministic policy for continuous action spaces.
 *
 * <p>The policy maps an input directly to a continuous action vector. The
 * neural network uses a hyperbolic tangent output activation, producing values
 * in {@code [-1, 1]}, which are then rescaled to the configured action
 * bounds.</p>
 *
 * <p>All action dimensions use the same lower and upper bounds.</p>
 */
public class MLPDeterministicPolicy implements DeterministicPolicyGradient {

    private final WrapperPolicyInputVector inputWrapper;
    private final NeuralNetwork network;
    private final int actionSize;
    private final double lowerBound;
    private final double upperBound;
    private ContinuousActionExplorationStrategy explorationStrategy;

    private MLKAgent agent;

    /**
     * Creates a deterministic neural policy.
     *
     * @param inputWrapper converts policy inputs to vectors
     * @param inputSize the input vector size
     * @param hiddenLayers the sizes of the hidden layers
     * @param actionSize the number of continuous action components
     * @param lowerBound the common lower bound of all action components
     * @param upperBound the common upper bound of all action components
     * @param explorationStrategy the exploration strategy for continuous actions, or {@code null} if none is used
     * @throws NullPointerException if {@code inputWrapper} is {@code null}
     * @throws IllegalArgumentException if a size is invalid, a hidden layer is
     *                                  empty, or the action bounds are invalid
     */
    public MLPDeterministicPolicy(WrapperPolicyInputVector inputWrapper, int inputSize, int[] hiddenLayers, 
    		int actionSize, double lowerBound, double upperBound, ContinuousActionExplorationStrategy explorationStrategy) {
        validateArguments(inputSize, hiddenLayers, actionSize, lowerBound, upperBound);

        this.inputWrapper = Objects.requireNonNull(inputWrapper, "inputWrapper");
        this.actionSize = actionSize;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;

        int[] architecture = buildArchitecture(inputSize, hiddenLayers, actionSize);
        this.network = new NeuralNetwork(architecture, NeuralNetwork.Activations.relu(), NeuralNetwork.Activations.tanh());
        this.explorationStrategy = explorationStrategy;
    }
    
    /**
     * Creates a deterministic neural policy without an exploration strategy.
     * @param inputWrapper converts policy inputs to vectors
     * @param inputSize the input vector size
     * @param hiddenLayers the sizes of the hidden layers
     * @param actionSize the number of continuous action components
     * @param lowerBound the common lower bound of all action components
     * @param upperBound the common upper bound of all action components
     * @throws NullPointerException if {@code inputWrapper} is {@code null}
     * @throws IllegalArgumentException if a size is invalid, a hidden layer is
     *                                  empty, or the action bounds are invalid
     */
    public MLPDeterministicPolicy(WrapperPolicyInputVector inputWrapper, int inputSize, int[] hiddenLayers, 
    		int actionSize, double lowerBound, double upperBound) {
        this(inputWrapper, inputSize, hiddenLayers, actionSize, lowerBound, upperBound, null);
    }

    /**
     * Initializes the policy and its neural-network parameters for the specified
     * agent.
     *
     * @param agent the agent using this policy
     * @throws NullPointerException if {@code agent} is {@code null}
     */
    @Override
    public void init(MLKAgent agent) {
        this.agent = Objects.requireNonNull(agent, "agent");
        network.initializeParameters(agent.prng());
    }

    /**
     * Returns the agent using this policy.
     *
     * @return the agent, or {@code null} if the policy has not been initialized
     */
    @Override
    public MLKAgent getAgent() {
        return agent;
    }

    /**
     * Selects the action associated with the specified input. 
     * 
     * <p> If an exploration strategy is set, it is applied to the action produced by the policy.</p>
     *
     * @param input the policy input
     * @return the continuous action produced by the policy
     */
    @Override
    public ActionContinuousVector selectAction(PolicyInput input) {
    	ActionContinuousVector action = forwardAction(input);
    	if (explorationStrategy != null) {
    		action = explorationStrategy.explore(action, agent.prng());
    	}
        return action;
    }

    /**
     * Computes the continuous action produced by the policy.
     *
     * <p>The raw network output is in {@code [-1, 1]} and is rescaled to
     * {@code [lowerBound, upperBound]}.</p>
     *
     * @param input the policy input
     * @return the bounded continuous action
     */
    @Override
    public ActionContinuousVector forwardAction(PolicyInput input) {
        double[] inputVector = inputWrapper.transform(input);
        double[] normalizedAction = network.forward(inputVector);
        double[] actionValues = rescaleAction(normalizedAction);

        return new ActionContinuousVector(actionValues, lowerBound, upperBound);
    }

    /**
     * Computes continuous actions for a batch of inputs.
     *
     * @param inputs the policy inputs
     * @return one continuous action for each input
     */
    @Override
    public ActionContinuousVector[] forwardActions(PolicyInput[] inputs) {
        ActionContinuousVector[] actions = new ActionContinuousVector[inputs.length];

        for (int i = 0; i < inputs.length; i++) {
            actions[i] = forwardAction(inputs[i]);
        }

        return actions;
    }

    /**
     * Updates the policy from a loss gradient with respect to the bounded action.
     *
     * <p>The provided gradient is converted from action space to normalized
     * network-output space before being backpropagated through the network.</p>
     *
     * @param input the policy input
     * @param dLossDAction the loss gradient with respect to each bounded action
     *                     component
     * @param learningRate the policy learning rate
     * @throws IllegalArgumentException if the gradient dimension does not match
     *                                  the action size
     */
    @Override
    public void updateFromActionGradient(PolicyInput input, double[] dLossDAction, double learningRate) {
        validateActionGradient(dLossDAction);

        double[] inputVector = inputWrapper.transform(input);
        double[] dLossDNormalizedAction = convertGradientToNormalizedSpace(dLossDAction);

        network.applyOutputGradient(inputVector, dLossDNormalizedAction, learningRate);
    }

    /**
     * Updates the policy from a batch of loss gradients with respect to bounded
     * continuous actions.
     *
     * @param inputs the policy inputs
     * @param dLossDActions the loss gradients with respect to the bounded action
     *                       vectors
     * @param learningRate the policy learning rate
     * @throws IllegalArgumentException if the batch sizes or gradient dimensions
     *                                  are inconsistent
     */
    @Override
    public void updateFromActionGradient(PolicyInput[] inputs, double[][] dLossDActions, double learningRate) {
        if (inputs.length != dLossDActions.length) {
            throw new IllegalArgumentException("Batch size mismatch between inputs and action gradients.");
        }

        double[][] inputVectors = new double[inputs.length][];
        double[][] normalizedGradients = new double[dLossDActions.length][];

        for (int i = 0; i < inputs.length; i++) {
            validateActionGradient(dLossDActions[i]);
            inputVectors[i] = inputWrapper.transform(inputs[i]);
            normalizedGradients[i] = convertGradientToNormalizedSpace(dLossDActions[i]);
        }

        network.applyOutputGradientBatch(inputVectors, normalizedGradients, learningRate);
    }
    

    @Override
    public void updateExplorationStrategy() {
        if (explorationStrategy != null) {
            explorationStrategy.update();
        }
    }

    /**
     * Returns a copy of the trainable neural-network parameters.
     *
     * @return the flattened network parameters
     */
    @Override
    public double[] getParameters() {
        return network.getParameters();
    }

    /**
     * Replaces the trainable neural-network parameters.
     *
     * @param parameters the flattened network parameters
     */
    @Override
    public void setParameters(double[] parameters) {
        network.setParameters(parameters);
    }

    /**
     * Returns the number of continuous action components produced by the policy.
     *
     * @return the action vector size
     */
    public int getActionSize() {
        return actionSize;
    }

    /**
     * Returns the common lower bound of the action components.
     *
     * @return the lower action bound
     */
    public double getLowerBound() {
        return lowerBound;
    }

    /**
     * Returns the common upper bound of the action components.
     *
     * @return the upper action bound
     */
    public double getUpperBound() {
        return upperBound;
    }

    @Override
    public ContinuousActionExplorationStrategy getExplorationStrategy() {
        return explorationStrategy;
    }

    @Override
    public void setExplorationStrategy(ContinuousActionExplorationStrategy explorationStrategy) {
        this.explorationStrategy = explorationStrategy;
    }
    
    
    /**
     * Rescales normalized action values from {@code [-1, 1]} to the configured
     * action interval.
     *
     * @param normalizedAction the normalized action values
     * @return the bounded action values
     */
    private double[] rescaleAction(double[] normalizedAction) {
        double[] action = new double[normalizedAction.length];
        double scale = (upperBound - lowerBound) / 2.0;
        double offset = (upperBound + lowerBound) / 2.0;

        for (int i = 0; i < normalizedAction.length; i++) {
            action[i] = offset + scale * normalizedAction[i];
        }

        return action;
    }

    /**
     * Converts a gradient with respect to the bounded action into a gradient
     * with respect to the normalized network output.
     *
     * @param dLossDAction the gradient in bounded action space
     * @return the gradient in normalized output space
     */
    private double[] convertGradientToNormalizedSpace(double[] dLossDAction) {
        double scale = (upperBound - lowerBound) / 2.0;
        double[] convertedGradient = new double[dLossDAction.length];

        for (int i = 0; i < dLossDAction.length; i++) {
            convertedGradient[i] = dLossDAction[i] * scale;
        }

        return convertedGradient;
    }

    /**
     * Builds the neural-network architecture.
     *
     * @param inputSize the input layer size
     * @param hiddenLayers the hidden-layer sizes
     * @param outputSize the output layer size
     * @return the complete network architecture
     */
    private int[] buildArchitecture(int inputSize, int[] hiddenLayers, int outputSize) {
        int hiddenCount = hiddenLayers == null ? 0 : hiddenLayers.length;
        int[] architecture = new int[hiddenCount + 2];

        architecture[0] = inputSize;

        if (hiddenLayers != null) {
            System.arraycopy(hiddenLayers, 0, architecture, 1, hiddenLayers.length);
        }

        architecture[architecture.length - 1] = outputSize;

        return architecture;
    }

    /**
     * Validates the policy configuration.
     *
     * @param inputSize the input vector size
     * @param hiddenLayers the hidden-layer sizes
     * @param actionSize the action vector size
     * @param lowerBound the lower action bound
     * @param upperBound the upper action bound
     */
    private void validateArguments(int inputSize, int[] hiddenLayers, int actionSize, double lowerBound, double upperBound) {
        if (inputSize <= 0) {
            throw new IllegalArgumentException("inputSize must be strictly positive.");
        }

        if (actionSize <= 0) {
            throw new IllegalArgumentException("actionSize must be strictly positive.");
        }

        if (!Double.isFinite(lowerBound) || !Double.isFinite(upperBound)) {
            throw new IllegalArgumentException("Action bounds must be finite.");
        }

        if (lowerBound >= upperBound) {
            throw new IllegalArgumentException("lowerBound must be strictly lower than upperBound.");
        }

        if (hiddenLayers != null && Arrays.stream(hiddenLayers).anyMatch(size -> size <= 0)) {
            throw new IllegalArgumentException("Hidden-layer sizes must be strictly positive.");
        }
    }

    /**
     * Validates an action-output gradient.
     *
     * @param gradient the gradient to validate
     * @throws NullPointerException if {@code gradient} is {@code null}
     * @throws IllegalArgumentException if its size does not match the action size
     */
    private void validateActionGradient(double[] gradient) {
        Objects.requireNonNull(gradient, "gradient");

        if (gradient.length != actionSize) {
            throw new IllegalArgumentException("Action gradient size mismatch: expected " + actionSize + " but got " + gradient.length + ".");
        }
    }
}
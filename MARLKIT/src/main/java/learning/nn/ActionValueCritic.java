package learning.nn;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.ActionContinuousVector;
import environment.observation.wrapperobservationvector.WrapperPolicyInputVector;
import experience.Experience;
import learning.Critic;
import learning.policies.Parameterized;
import learning.policies.PolicyInput;

/**
 * Action-value critic used by deterministic actor-critic algorithms.
 *
 * <p>This critic approximates the action-value function {@code Q(o, a)}, where
 * {@code o} is an observation and {@code a} is a continuous action vector. The
 * observation and action vectors are concatenated before being passed to the
 * neural network.</p>
 *
 * <p>The critic can be trained toward an externally computed target value and
 * can compute the gradient of its estimate with respect to the action. This
 * action gradient can be used to update a deterministic actor.</p>
 */
public class ActionValueCritic implements Critic, Parameterized {

    private final NeuralNetwork network;
    private final WrapperPolicyInputVector observationWrapper;
    private final int observationSize;
    private final int actionSize;

    protected MLKAgent agent;
    protected Map<Experience, Experience> enrichedExperienceMap;

    /**
     * Creates an action-value critic with one hidden layer.
     *
     * @param observationSize the size of the observation vector
     * @param actionSize the size of the continuous action vector
     * @param hiddenSize the number of neurons in the hidden layer
     * @param observationWrapper converts observations to vectors
     * @throws NullPointerException if {@code observationWrapper} is {@code null}
     * @throws IllegalArgumentException if one of the specified sizes is not
     *                                  strictly positive
     */
    public ActionValueCritic(int observationSize, int actionSize, int hiddenSize, WrapperPolicyInputVector observationWrapper) {
        if (observationSize <= 0) {
            throw new IllegalArgumentException("observationSize must be strictly positive.");
        }
        if (actionSize <= 0) {
            throw new IllegalArgumentException("actionSize must be strictly positive.");
        }
        if (hiddenSize <= 0) {
            throw new IllegalArgumentException("hiddenSize must be strictly positive.");
        }

        this.observationSize = observationSize;
        this.actionSize = actionSize;
        this.observationWrapper = Objects.requireNonNull(observationWrapper, "observationWrapper");
        this.network = NeuralNetwork.reluIdentity(new int[] { observationSize + actionSize, hiddenSize, 1 });
        this.enrichedExperienceMap = new HashMap<>();
    }

    /**
     * Initializes the critic for the specified agent and initializes its neural
     * network parameters.
     *
     * @param agent the agent using this critic
     * @throws NullPointerException if {@code agent} is {@code null}
     */
    @Override
    public void init(MLKAgent agent) {
        this.agent = Objects.requireNonNull(agent, "agent");
        network.initializeParameters(prng());
    }

    /**
     * Returns the pseudo-random number generator provided by the agent.
     *
     * @return the agent's pseudo-random number generator
     */
    protected RandomGenerator prng() {
        return agent.prng();
    }

    @Override
    public void enrichExperience(Experience originalExperience, Experience enrichedExperience) {
        enrichedExperienceMap.put(originalExperience, enrichedExperience);
    }

    @Override
    public Experience getEnrichedExperience(Experience originalExperience) {
        return enrichedExperienceMap.get(originalExperience);
    }

    @Override
    public void removeEnrichedExperience(Experience originalExperience) {
        enrichedExperienceMap.remove(originalExperience);
    }

    @Override
    public void clearEnrichedExperiences() {
        enrichedExperienceMap.clear();
    }

    /**
     * Estimates the action value {@code Q(o, a)}.
     *
     * @param observation the observation
     * @param action the continuous action
     * @return the estimated action value
     */
    public double getValue(PolicyInput observation, ActionContinuousVector action) {
        double[] criticInput = buildCriticInput(observation, action);
        return getValue(criticInput);
    }

    /**
     * Estimates an action value from an already constructed critic input.
     *
     * <p>The input must contain the observation components followed by the
     * action components.</p>
     *
     * @param criticInput the concatenated observation-action vector
     * @return the estimated action value
     */
    public double getValue(double[] criticInput) {
        return network.predict(criticInput)[0];
    }

    /**
     * Updates the critic toward the specified target value.
     *
     * <p>The critic minimizes the squared error between {@code Q(o, a)} and the
     * supplied target value.</p>
     *
     * @param observation the observation
     * @param action the continuous action
     * @param targetValue the target action value
     * @param learningRate the critic learning rate
     * @return the prediction error {@code targetValue - Q(o, a)}
     */
    public double updateTowardTarget(PolicyInput observation, ActionContinuousVector action, double targetValue, double learningRate) {
        double[] criticInput = buildCriticInput(observation, action);
        double prediction = getValue(criticInput);
        double error = targetValue - prediction;
        double[] dLossDOutput = new double[] { 2.0 * (prediction - targetValue) };

        network.applyOutputGradient(criticInput, dLossDOutput, learningRate);

        return error;
    }

    /**
     * Computes the gradient of {@code Q(o, a)} with respect to the action.
     *
     * <p>The returned vector contains one derivative for each action component:</p>
     *
     * <pre>
     * dQ(o, a) / da
     * </pre>
     *
     * <p>This method does not modify the critic's parameters.</p>
     *
     * @param observation the observation
     * @param action the continuous action at which the gradient is evaluated
     * @return the gradient of the estimated value with respect to the action
     */
    public double[] actionGradient(PolicyInput observation, ActionContinuousVector action) {
        double[] criticInput = buildCriticInput(observation, action);
        double[] inputGradient = network.inputGradient(criticInput, new double[] { 1.0 });

        return Arrays.copyOfRange(inputGradient, observationSize, observationSize + actionSize);
    }

    /**
     * Builds the neural-network input by concatenating the observation vector
     * and the continuous action vector.
     *
     * @param observation the observation
     * @param action the continuous action
     * @return the concatenated observation-action vector
     * @throws IllegalArgumentException if the observation or action dimension
     *                                  does not match the critic configuration
     */
    protected double[] buildCriticInput(PolicyInput observation, ActionContinuousVector action) {
        Objects.requireNonNull(observation, "observation");
        Objects.requireNonNull(action, "action");

        double[] observationVector = observationWrapper.transform(observation);
        double[] actionVector = action.getValues();

        if (observationVector.length != observationSize) {
            throw new IllegalArgumentException("Observation size mismatch: expected " + observationSize + " but got " + observationVector.length + ".");
        }
        if (actionVector.length != actionSize) {
            throw new IllegalArgumentException("Action size mismatch: expected " + actionSize + " but got " + actionVector.length + ".");
        }

        double[] criticInput = new double[observationSize + actionSize];
        System.arraycopy(observationVector, 0, criticInput, 0, observationSize);
        System.arraycopy(actionVector, 0, criticInput, observationSize, actionSize);

        return criticInput;
    }

    /**
     * Returns the size of the observation portion of the critic input.
     *
     * @return the observation vector size
     */
    public int getObservationSize() {
        return observationSize;
    }

    /**
     * Returns the size of the action portion of the critic input.
     *
     * @return the continuous action vector size
     */
    public int getActionSize() {
        return actionSize;
    }

    /**
     * Returns a copy of the critic's trainable parameters.
     *
     * @return the flattened neural-network parameters
     */
    @Override
    public double[] getParameters() {
        return network.getParameters();
    }

    /**
     * Replaces the critic's trainable parameters.
     *
     * @param parameters the flattened neural-network parameters
     */
    @Override
    public void setParameters(double[] parameters) {
        network.setParameters(parameters);
    }
}
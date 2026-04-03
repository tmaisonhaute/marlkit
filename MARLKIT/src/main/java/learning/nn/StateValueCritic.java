package learning.nn;

import java.util.random.RandomGenerator;

import environment.observation.Observation;
import environment.observation.wrapperobservationvector.WrapperPolicyInputVector;

/**
 * State-value critic used in actor-critic methods.
 *
 * <p>This critic approximates the state-value function V(s).
 * It can be updated either:
 * <ul>
 *   <li>towards an externally provided target value, or</li>
 *   <li>from a transition using a TD(0) target:
 *       target = r + gamma * V(s')</li>
 * </ul>
 */
public class StateValueCritic {

    private final NeuralNetwork network;
    private final WrapperPolicyInputVector observationWrapper;
    private double learningRate;

    /**
     * Creates a state-value critic with one hidden layer.
     *
     * @param inputSize the observation vector size
     * @param hiddenSize the number of hidden neurons
     * @param prng the random number generator
     * @param learningRate the critic learning rate
     * @param observationWrapper converts observations to vectors
     */
    public StateValueCritic(
            int inputSize,
            int hiddenSize,
            RandomGenerator prng,
            double learningRate,
            WrapperPolicyInputVector observationWrapper) {

        this.observationWrapper = observationWrapper;
        this.learningRate = learningRate;

        // Architecture: input -> hidden -> scalar state value V(s)
        this.network = NeuralNetwork.reluIdentity(new int[] { inputSize, hiddenSize, 1 });
        this.network.initializeParameters(prng);
    }

    /**
     * Estimates the state value V(s).
     *
     * @param observation the state observation
     * @return the estimated state value
     */
    public double getValue(Observation observation) {
        double[] observationVector = observationWrapper.transform(observation);
        return getValue(observationVector);
    }

    /**
     * Estimates the state value V(s) from an already transformed observation vector.
     *
     * @param observationVector the observation vector
     * @return the estimated state value
     */
    public double getValue(double[] observationVector) {
        return network.predict(observationVector)[0];
    }

    /**
     * Computes the TD(0) target:
     * target = reward + gamma * V(nextState), or target = reward if terminal.
     *
     * @param reward the immediate reward
     * @param nextObservation the next state observation
     * @param terminal whether the transition ends the episode
     * @param gamma the discount factor
     * @return the TD target value
     */
    public double computeTdTarget(
            double reward,
            Observation nextObservation,
            boolean terminal,
            double gamma) {

        if (terminal) {
            return reward;
        }

        return reward + gamma * getValue(nextObservation);
    }

    /**
     * Computes the TD error:
     * delta = reward + gamma * V(nextState) - V(state)
     *
     * @param observation the current state observation
     * @param reward the immediate reward
     * @param nextObservation the next state observation
     * @param terminal whether the transition ends the episode
     * @param gamma the discount factor
     * @return the TD error
     */
    public double computeTdError(
            Observation observation,
            double reward,
            Observation nextObservation,
            boolean terminal,
            double gamma) {

        double currentValue = getValue(observation);
        double targetValue = computeTdTarget(reward, nextObservation, terminal, gamma);
        return targetValue - currentValue;
    }

    /**
     * Performs a TD(0) update from a transition:
     * target = reward + gamma * V(nextState), or reward if terminal.
     *
     * <p>The returned value is the TD error:
     * delta = target - V(state)
     *
     * @param observation the current state observation
     * @param reward the immediate reward
     * @param nextObservation the next state observation
     * @param terminal whether the transition ends the episode
     * @param gamma the discount factor
     * @return the TD error
     */
    public double updateFromTransition(
            Observation observation,
            double reward,
            Observation nextObservation,
            boolean terminal,
            double gamma) {

        double[] observationVector = observationWrapper.transform(observation);

        double currentValue = getValue(observationVector);
        double targetValue = computeTdTarget(reward, nextObservation, terminal, gamma);

        double tdError = targetValue - currentValue;

        applyRegressionUpdate(observationVector, targetValue);

        return tdError;
    }

    /**
     * Updates the critic towards a provided scalar target value.
     *
     * <p>This is useful if the TD target was already computed elsewhere.
     *
     * @param observation the current state observation
     * @param targetValue the target value for V(s)
     * @return the prediction error targetValue - V(s)
     */
    public double updateTowardTarget(Observation observation, double targetValue) {
        double[] observationVector = observationWrapper.transform(observation);
        double currentValue = getValue(observationVector);
        double error = targetValue - currentValue;

        applyRegressionUpdate(observationVector, targetValue);

        return error;
    }

    /**
     * Applies a supervised regression update on V(s).
     * 
     * MSE loss : L = (prediction - target). dL/dy = 2*(prediction - target)
     *
     * @param observationVector the input vector
     * @param targetValue the scalar target value
     */
    private void applyRegressionUpdate(double[] observationVector, double targetValue) {
        double prediction = getValue(observationVector);
        
        double[] dLossDOutput = new double[] { 2.0 * (prediction - targetValue) };

        network.applyOutputGradient(observationVector, dLossDOutput, learningRate);
    }

    /**
     * Sets the critic learning rate.
     *
     * @param learningRate the new learning rate
     */
    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    /**
     * Returns the current critic learning rate.
     *
     * @return the learning rate
     */
    public double getLearningRate() {
        return learningRate;
    }
}

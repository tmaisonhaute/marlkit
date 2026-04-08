package learning.nn;

import java.util.HashMap;
import java.util.Map;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import environment.observation.wrapperobservationvector.WrapperPolicyInputVector;
import learning.Critic;
import learning.Experience;
import learning.policy.PolicyInput;
import madkit.simulation.SimuAgent;

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
public class StateValueCritic implements Critic {

    private final NeuralNetwork network;
    private final WrapperPolicyInputVector observationWrapper;
    protected MLKAgent agent;
    protected Map<Experience, Experience> enrichedExperienceMap;

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
            WrapperPolicyInputVector observationWrapper) {

        this.observationWrapper = observationWrapper;

        // Architecture: input -> hidden -> scalar state value V(s)
        this.network = NeuralNetwork.reluIdentity(new int[] { inputSize, hiddenSize, 1 });
        
        enrichedExperienceMap = new HashMap<>();
    }
    
    @Override
    public void init(MLKAgent agent) {
    	this.agent = agent;
    	this.network.initializeParameters(prng());
    }
    
    protected RandomGenerator prng() {
		return agent.prng();
	}
    
    @Override
    public void enrichExperience(Experience originalExperience, Experience enrichedExperience) {
    	enrichedExperienceMap.put(originalExperience, enrichedExperience);
    }
    
    @Override
	public void resetEnrichedExperiences() {
		enrichedExperienceMap.clear();
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
     * Estimates the state value V(s).
     *
     * @param observation the state observation
     * @return the estimated state value
     */
    public double getValue(PolicyInput observation) {
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
    	((SimuAgent) agent).getLogger().info("State value : " + network.predict(observationVector)[0]);
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
            PolicyInput nextObservation,
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
    		PolicyInput observation,
            double reward,
            PolicyInput nextObservation,
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
            PolicyInput observation,
            double reward,
            PolicyInput nextObservation,
            boolean terminal,
            double gamma, 
            double learningRate) {
    	
        double[] observationVector = observationWrapper.transform(observation);

        double currentValue = getValue(observationVector);
        double targetValue = computeTdTarget(reward, nextObservation, terminal, gamma);

        double tdError = targetValue - currentValue;

        applyRegressionUpdate(observationVector, targetValue, learningRate);

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
    public double updateTowardTarget(PolicyInput observation, double targetValue, double learningRate) {
        double[] observationVector = observationWrapper.transform(observation);
        double currentValue = getValue(observationVector);
        double error = targetValue - currentValue;

        applyRegressionUpdate(observationVector, targetValue, learningRate);

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
    private void applyRegressionUpdate(double[] observationVector, double targetValue, double learningRate) {
        double prediction = getValue(observationVector);
        
        double[] dLossDOutput = new double[] { 2.0 * (prediction - targetValue) };

        network.applyOutputGradient(observationVector, dLossDOutput, learningRate);
    }

}

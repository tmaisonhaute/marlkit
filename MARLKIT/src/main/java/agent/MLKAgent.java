package agent;

import java.util.random.RandomGenerator;

import agent.action.Action;
import environment.observation.Observation;
import environment.reward.Reward;
import learning.Experience;
import learning.algorithm.Algorithm;
import learning.policy.Policy;

/**
 * Core interface for agents in the MARLKIT framework.
 * Defines the fundamental behavior and lifecycle methods for reinforcement learning agents.
 */
public interface MLKAgent {
	
	/**
	 * Returns the policy used by this agent for decision making.
	 *
	 * @return the agent's policy
	 */
	public Policy getPolicy();

	/**
	 * Returns the algorithm used by this agent for learning.
	 * @return the agent's learning algorithm
	 */
	public Algorithm getAlgorithm();
	
	/**
	 * Returns the pseudo-random number generator used by this agent.
	 *
	 * @return the agent's random number generator
	 */
	public RandomGenerator prng();
	
	/**
	 * Sends agent information to the environment during initialization.
	 */
	public void sendInfo();
	
	/**
	 * Sets the policy for this agent.
	 *
	 * @param policy the policy to use
	 */
	public void setPolicy(Policy policy);

	/**
	 * Sets the learning algorithm for this agent.
	 * @param algorithm the algorithm to use
	 */
	public void setAlgorithm(Algorithm algorithm);
	
	/**
	 * Initializes the agent's policy with necessary parameters.
	 */
	public void initializeAll();
	
	/**
	 * Records an experience composed of observation, action, and reward.
	 *
	 * @param obs the observation received
	 * @param act the action taken
	 * @param rew the reward received
	 */
	public abstract void feedbackExperience(Observation obs, Action act, Reward rew);
	
	/**
	 * Records a complete experience object.
	 *
	 * @param experience the experience to record
	 */
	public abstract void feedbackExperience(Experience experience);
	
	/**
	 * Selects and returns an action based on the given observation.
	 *
	 * @param obs the observation to act upon
	 * @return the selected action
	 */
	public Action takeAction(Observation obs);
	
	/**
	 * Updates the policy based on accumulated experience at the given timestep.
	 *
	 * @param timestep the current timestep in the simulation
	 */
	public void updatePolicy(int timestep);
	
	/**
	 * Performs learning on the accumulated batch of experiences.
	 */
	public void learnOnBatch();
	
	/**
	 * Signals the end of an episode and performs any necessary cleanup or learning.
	 */
	public void endEpisode();

	
}

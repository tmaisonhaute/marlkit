package agent;

import java.util.random.RandomGenerator;

import agent.action.Action;
import communication.CommunicationModule;
import environment.MLKEnvironment;
import environment.observation.Observation;
import environment.reward.Reward;
import learning.Experience;
import learning.algorithm.Algorithm;
import learning.policy.Policy;
import learning.policy.PolicyInput;

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
	 * Returns the communication module used by this agent for interacting with other agents.
	*/
	public CommunicationModule getCommunicationModule();
	
	public default Observation getObservation() {
		return getMLKEnvironment().getObservation(this);
	}
	
	
	/**
	 * Returns the pseudo-random number generator used by this agent.
	 *
	 * @return the agent's random number generator
	 */
	public RandomGenerator prng();
	
	/**
	 * Sends agent information to the environment during initialization.
	 */
	public void notifySelfToEnvironment();
	
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
	 * Sets the communication module for this agent.
	 * @param communicationModule the communication module to use
	 */
	public void setCommunicationModule(CommunicationModule communicationModule);

	/**
	 * Initializes the agent's policy and algorithm with necessary parameters.
	 */
	public void initializeAll();
	
	/**
	 * Records an experience composed of observation, action, and reward.
	 *
	 * @param input the PolicyInput received
	 * @param act the action taken
	 * @param rew the reward received
	 */
	public abstract void feedbackExperience(PolicyInput input, Action act, Reward rew);
	
	/**
	 * Records a complete experience object.
	 *
	 * @param experience the experience to record
	 */
	public abstract void feedbackExperience(Experience experience);
	
	/** 
	 * Communicates with other agents.
	*/
	public default void communicate(){
		getCommunicationModule().communicate(this);
	}
	
	/**
	 * Observes the environment, selects an action, and sends it as an influence to the environment.
	 */
	public void takeAction();
	
	/**
	 * Retrieves the experience from the environment and records it.
	 */
	public void collectExperience();
	
	/**
	 * Returns the environment this agent belongs to, cast to {@link MLKEnvironment}.
	 *
	 * @return the agent's environment
	 */
	public MLKEnvironment getMLKEnvironment();
	
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

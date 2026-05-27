package agent;

import java.util.random.RandomGenerator;

import agent.action.Action;
import environment.MLKEnvironment;
import environment.observation.Observation;
import learning.Experience;
import learning.algorithm.Algorithm;
import learning.policy.Policy;
import learning.policy.PolicyInput;
import madkit.simulation.SimuAgent;
import reward.Reward;

/**
 * Core interface for agents in the MARLKIT framework.
 * Defines the fundamental behavior and lifecycle methods for reinforcement learning agents.
 */
public interface MLKAgent {
	public static final String DEFAULT_AGENT_ROLE = "MLKAgent";
	
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
	 * Returns the current observation provided by the environment for this agent.
	 * @return the current observation for this agent
	 */
	public default Observation getObservation() {
		return getMLKEnvironment().getObservation(this);
	}
	
	/**
	 * Registers the current observation for this agent, which will be used for action selection in the current timestep.
	 * 
	 */
	public default void registerObservation() {
		setRegisteredObservation(getObservation());
	}
	
	/**
	 * Returns the registered observation used by this agent for action selection for this timestep.
	 * @return the registered observation for this agent
	 */
	public Observation getRegisteredObservation();

	/**
	 * Sets the registered observation for this agent for this timestep, which will be used for action selection.
	 * @param registeredObservation the registered observation to set
	 */
	public void setRegisteredObservation(Observation registeredObservation);
	
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
	 * Selects an action based on the given policy input.
	 * @param input the policy input to use for action selection
	 * @return the selected action
	 */
	public Action selectAction(PolicyInput input);
	
	/**
	 * Observes the environment, selects an action, and sends it as an influence to the environment.
	 */
	public void takeAction();
	
	/**
	 * Retrieves the experience from the environment and records it.
	 */
	public void collectExperience();
	
	/**
	 * Get the experience from the environment, not the accumulated experience in the agent's memory.
	 * @return the experience from the environment
	 */
	public Experience getEnvExperience();
	
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
	
	public SimuAgent getSimuAgent();

	public default String getRole() {
		return DEFAULT_AGENT_ROLE;
	}
	
}

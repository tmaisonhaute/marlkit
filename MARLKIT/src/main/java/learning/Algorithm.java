package learning;

import java.util.random.RandomGenerator;

import agent.MLKAgent;
import madkit.kernel.AgentLogger;

public interface Algorithm {
	
	/**
	 * Initializes the Algorithm with the agent that will use it.
	 * Also initializes the policy associated with this algorithm.
	 *
	 * @param agent the agent using this algorithm
	 */
	public default void init(MLKAgent agent) {
		setAgent(agent);
	}

	/**
	 * Sets the policy used by this Algorithm.
	 * @param policy the policy to set
	 */
	public void setPolicy(Policy policy);
	
	/**
	 * Returns the policy used by this Algorithm.
	 *
	 * @return the policy
	 */
	public Policy getPolicy();
	
	/**
	 * Returns the agent using this Algorithm.
	 *
	 * @return the agent
	 */
	public MLKAgent getAgent();
	
	/**
	 * Sets the agent that will use this Algorithm.
	 * @param agent the agent to set
	 */
	public void setAgent(MLKAgent agent); 
	
	/**
	 * Returns the pseudo-random number generator from the agent.
	 *
	 * @return the random number generator
	 */
	public default RandomGenerator pnrg() {
		return getAgent().prng();
	}
	
	/**
	 * Returns the frequency (in timesteps) at which learning should occur.
	 * A value of 0 means no periodic learning.
	 *
	 * @return the learning frequency
	 */
	public int getLearningFrequency();
	
	/**
	 * Indicates whether the algorithm should learn from the currently accumulated
	 * batch at the specified simulation step.
	 *
	 * <p>The default implementation uses the learning frequency returned by
	 * {@link #getLearningFrequency()}. A non-positive frequency disables periodic
	 * learning.</p>
	 *
	 * @param timestep the current simulation step
	 * @param batch the batch of accumulated experiences
	 * @return {@code true} if learning should occur
	 */
	default boolean shouldLearn(int timestep, Batch batch) {
	    int frequency = getLearningFrequency();
	    return frequency > 0 && (timestep + 1) % frequency == 0;
	}
	
	
	/**
	 * Performs learning using a batch of experiences.
	 *
	 * @param batch the batch of experiences to learn from
	 * @param logger the agent's logger for debug information
	 */
	public void learnOnBatch(Batch batch, AgentLogger logger);
	
	/**
	 * Called at the end of an episode to perform episode-level learning or cleanup.
	 *
	 * @param batch the batch of experiences from the episode
	 * @param logger the agent's logger for debug information
	 */
	public void endEpisode(Batch batch, AgentLogger logger);
	
}

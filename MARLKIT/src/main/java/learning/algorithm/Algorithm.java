package learning.algorithm;

import java.util.random.RandomGenerator;

import agent.MLKAgent;
import learning.Batch;
import learning.policy.Policy;
import madkit.kernel.AgentLogger;

public interface Algorithm {
	
	/**
	 * Initializes the Algorithm with the agent that will use it.
	 *
	 * @param agent the agent using this algorithm
	 */
	public abstract void init(MLKAgent agent);

	/**
	 * Sets the policy used by this Algorithm.
	 * @param policy the policy to set
	 */
	public abstract void setPolicy(Policy policy);
	
	/**
	 * Returns the policy used by this Algorithm.
	 *
	 * @return the policy
	 */
	public abstract Policy getPolicy();
	
	/**
	 * Returns the agent using this Algorithm.
	 *
	 * @return the agent
	 */
	public abstract MLKAgent getAgent();
	
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
	public abstract int getLearningFrequency();
	
	
	/**
	 * Performs learning using a batch of experiences.
	 *
	 * @param batch the batch of experiences to learn from
	 * @param logger the agent's logger for debug information
	 */
	public abstract void learnOnBatch(Batch batch, AgentLogger logger);
	
	/**
	 * Called at the end of an episode to perform episode-level learning or cleanup.
	 *
	 * @param batch the batch of experiences from the episode
	 * @param logger the agent's logger for debug information
	 */
	public abstract void endEpisode(Batch batch, AgentLogger logger);
	
}

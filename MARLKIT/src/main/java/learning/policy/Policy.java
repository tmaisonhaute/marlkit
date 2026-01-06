package learning.policy;
import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import learning.Batch;
import madkit.kernel.AgentLogger;

/**
 * Represents a policy that defines how an agent selects actions based on observations.
 */
public interface Policy {
	
	/**
	 * Initializes the policy with the agent that will use it.
	 *
	 * @param agent the agent using this policy
	 */
	public abstract void init(MLKAgent agent);
	
	/**
	 * Returns the agent using this policy.
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
     * Takes an action based on a single observation.
     * 
     * @param observation the observation based on which the action is taken
     * @return the action taken
     */
	public abstract Action takeAction(Observation observation);

	/**
     * Takes a list of actions based on a list of observations.
     * 
     * @param observations the list of observations based on which the actions are taken
     * @return the list of actions taken
     */
	public default List<Action> takeActionsList(List<Observation> observations){
		List<Action> actions = new ArrayList<>();
		for(Observation o : observations) {
			actions.add(takeAction(o));
		}
		return actions;
	}

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

//	default Map<Pair<Observation, Action>, Double> getQ(){
//		Map<Pair<Observation, Action>, Double> q = new HashMap<>();
//		return q;
//	};
}



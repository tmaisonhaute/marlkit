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
 * Represents a policy that an agent follows to take actions based on observations.
 */
public interface Policy {
	
	public abstract void init(MLKAgent agent);
	public abstract MLKAgent getAgent();
	public default RandomGenerator pnrg() {
		return getAgent().prng();
	}
	
	/** 
	 * Returns the frequency of learning.
	 * 0 means no call for learnOnBatch
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
     * Learns from a batch of data.
     * 
     * @param batch the batch of data to learn from
     */
	public abstract void learnOnBatch(Batch batch, AgentLogger logger);
	
	/**
     * Ends an episode.
     * 
     * @param batch the batch of data to learn from
     * @param logger the logger of the agent
     */
	public abstract void endEpisode(Batch batch, AgentLogger logger);

//	default Map<Pair<Observation, Action>, Double> getQ(){
//		Map<Pair<Observation, Action>, Double> q = new HashMap<>();
//		return q;
//	};
}



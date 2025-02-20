package learning.policy;
import java.util.ArrayList;
import java.util.List;

import agent.action.Action;
import environment.observation.Observation;
import learning.Batch;
import madkit.kernel.AgentLogger;

/**
 * Represents a policy that an agent follows to take actions based on observations.
 */
public interface Policy {
	/**
     * Takes an action based on a single observation.
     * 
     * @param obs the observation based on which the action is taken
     * @return the action taken
     */
	public abstract Action takeAction(Observation obs);

	/**
     * Takes a list of actions based on a list of observations.
     * 
     * @param observations the list of observations based on which the actions are taken
     * @return the list of actions taken
     */
	public default List<Action> takeActionsList(List<Observation> observations){
		List<Action> actions = new ArrayList<Action>();
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
}



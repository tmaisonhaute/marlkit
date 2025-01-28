package learning;
import java.util.List;

import agent.Action;
import environment.Observation;

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
	public abstract List<Action> takeActionsList(List<Observation> observations);

	/**
     * Learns from a batch of data.
     * 
     * @param batch the batch of data to learn from
     */
	public abstract void learn_on_batch(Batch batch);
}



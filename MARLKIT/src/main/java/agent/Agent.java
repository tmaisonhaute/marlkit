package agent;
import java.util.List;

import agent.action.Action;
import environment.observation.Observation;
import learning.policy.Policy;


/**
 * Represents an agent in the multi-agent system.
 */
public abstract class Agent {
	public Policy policy;

	/**
     * Takes an action based on a single observation.
     * 
     * @param obs the observation based on which the action is taken
     * @return the action taken
     */
	public Action takeAction(Observation obs) {
		return policy.takeAction(obs);
		/*
		 * on get l'env, 
		 * On prend l'observation
		 * 
		 */
	}

	/**
     * Takes a list of actions based on a list of observations.
     * 
     * @param observations the list of observations based on which the actions are taken
     * @return the list of actions taken
     */
	public List<Action> takeActionList(List<Observation> observations){
		return policy.takeActionsList(observations);
	}
	
}

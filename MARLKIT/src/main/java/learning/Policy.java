package learning;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;

/**
 * Policy interface for reinforcement learning agents. A policy is a function that maps observations to actions.
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
	public default RandomGenerator prng() {
		return getAgent().prng();
	}

	/**
     * Selects an action based on a single observation.
     * 
     * @param observation based on which the action is taken
     * @return the action taken
     */
	public abstract Action selectAction(Observation observation);

	/**
     * Takes a list of actions based on a list of Observation.
     * 
     * @param observations the list of Observations based on which the actions are taken
     * @return the list of actions taken
     */
	public default List<Action> takeActionsList(List<Observation> observations){
		List<Action> actions = new ArrayList<>();
		for(Observation i : observations) {
			actions.add(selectAction(i));
		}
		return actions;
	}

}

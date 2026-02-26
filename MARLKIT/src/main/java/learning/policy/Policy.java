package learning.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;

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
	public default RandomGenerator pnrg() {
		return getAgent().prng();
	}

	/**
     * Takes an action based on a single input (usually Observation).
     * 
     * @param input based on which the action is taken
     * @return the action taken
     */
	public abstract Action selectAction(PolicyInput input);

	/**
     * Takes a list of actions based on a list of PolicyInput (usually Observation).
     * 
     * @param inputs the list of PolicyInputs based on which the actions are taken
     * @return the list of actions taken
     */
	public default List<Action> takeActionsList(List<PolicyInput> inputs){
		List<Action> actions = new ArrayList<>();
		for(PolicyInput i : inputs) {
			actions.add(selectAction(i));
		}
		return actions;
	}

}

package learning.policies.valuefunction;

import agent.action.Action;
import agent.action.ActionSpace;
import environment.observation.Observation;
import util.Pair;

/**
 * An interface for evaluating the value of actions given a specific policy input (observation).
 */
public interface ActionEvaluator {

	/**
	 * Returns the value of the specified action given the provided policy input (observation).
	 * @param input the policy input (observation) for which the action's value is to be evaluated
	 * @param action the action whose value is to be evaluated
	 * @return the value of the action given the policy input (observation)
	 */
    public Double getValue(Observation input, Action action);


    /**
     * Returns the value of the specified action given the provided policy input (observation) as a Pair.
     * @param key a Pair containing the policy input (observation) and the action whose value is to be evaluated
     * @return the value of the action given the policy input (observation)
     */
    public default Double getValue(Pair<Observation, Action> key) {
        return getValue(key.getFirst(), key.getSecond());
    }
    
    /**
     * Returns a copy of the ActionSpace containing all actions that have been associated with the given observation in the value function.
     * @param observation
     * @return a copy of the ActionSpace containing all actions associated with the given observation
     */
    public ActionSpace getActionSpace(Observation observation);
}

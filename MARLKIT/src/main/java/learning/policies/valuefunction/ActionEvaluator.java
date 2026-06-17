package learning.policies.valuefunction;

import agent.action.Action;
import agent.action.ActionSpace;
import learning.policies.PolicyInput;
import util.Pair;

public interface ActionEvaluator {

    public Double getValue(PolicyInput input, Action action);


    public default Double getValue(Pair<PolicyInput, Action> key) {
        return getValue(key.getFirst(), key.getSecond());
    }
    
    /**
     * Returns a copy of the ActionSpace containing all actions that have been associated with the given observation in the value function.
     * @param observation
     * @return
     */
    public ActionSpace getActionSpace(PolicyInput observation);
}

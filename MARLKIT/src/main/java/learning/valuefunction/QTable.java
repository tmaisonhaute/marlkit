package learning.valuefunction;

import agent.action.Action;
import learning.policy.PolicyInput;
import util.Pair;

/**
 * Tabular Q-value function Q(s, a) for state-action pairs.
 * <p>
 * Maps observation-action pairs to their expected cumulative reward.
 * Used in value-based reinforcement learning algorithms like Q-Learning and SARSA.
 * </p>
 *
 * @see ValueFunction
 */
public class QTable extends ValueFunction<Pair<PolicyInput, Action>> {

	/**
	 * Creates a Q-table with the specified default value for unseen state-action pairs.
	 *
	 * @param defaultQValue the default Q-value for unvisited state-action pairs
	 */
	public QTable(double defaultQValue) {
		super(defaultQValue);
	}

	/**
	 * Returns Q(s, a) for the given input and action.
	 *
	 * @param PolicyInput the input (state/observation)
	 * @param action      the action
	 * @return the Q-value, or the default value if this pair has not been visited
	 */
	public Double getValue(PolicyInput input, Action action) {
        return tableValue.getOrDefault(new Pair<>(input, action), getDefaultValue());
    }

}

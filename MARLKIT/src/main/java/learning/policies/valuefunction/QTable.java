package learning.policies.valuefunction;

import agent.action.Action;
import agent.action.ActionSpace;
import environment.observation.Observation;
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
public class QTable extends ValueFunction<Pair<Observation, Action>> implements ActionEvaluator {

	/**
	 * Creates a Q-table with the specified default value for unseen state-action pairs.
	 *
	 * @param defaultQValue the default Q-value for unvisited state-action pairs
	 */
	public QTable(double defaultQValue) {
		super(defaultQValue);
	}

	@Override
	/**
	 * Returns Q(s, a) for the given input and action.
	 *
	 * @param Observation the input (state/observation)
	 * @param action      the action
	 * @return the Q-value, or the default value if this pair has not been visited
	 */
	public Double getValue(Observation input, Action action) {
		  return getValue(new Pair<>(input, action));
    }

	@Override
	protected Pair<Observation, Action> copyKey(Pair<Observation, Action> key) {
	    return new Pair<>(
	        key.getFirst().copy(),
	        key.getSecond().copy()
	    );
	}

	@Override
	/**
	 * Returns the set of actions that have been associated with the given
	 * observation in the Q-table.
	 *
	 * @param observation the observation (state)
	 * @return an ActionSpace containing all actions that have been seen with this
	 *         observation
	 */
	public ActionSpace getActionSpace(Observation observation) {
		ActionSpace actionSpace = new ActionSpace();
		for (Pair<Observation, Action> key : tableValue.keySet()) {
			if (key.getFirst().equals(observation)) {
				actionSpace.addAction(key.getSecond().copy());
			}
		}
		
		return actionSpace;
		
	}
	
	

}

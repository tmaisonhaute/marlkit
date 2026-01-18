package learning.policy.deprecated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.Observation;
import learning.Batch;
import madkit.kernel.AgentLogger;
import util.Pair;

/**
 * Abstract base class for Q-value based reinforcement learning policies.
 * <p>
 * This class provides common functionality for policies that maintain a Q-table
 * mapping state-action pairs to expected cumulative rewards. It extends
 * {@link PolicyEpsilon} to support epsilon-greedy exploration.
 * </p>
 * <p>
 * Action selection uses epsilon-greedy: with probability epsilon, a random action
 * is chosen; otherwise, the action with the highest Q-value is selected.
 * </p>
 *
 * @see PolicyEpsilon
 * @see PolicyQLearning
 * @see PolicySarsa
 * @see PolicyMonteCarlo
 */
public abstract class PolicyQValueBased extends PolicyEpsilon {

	private Map<Pair<Observation, Action>, Double> q;
	private List<Action> actionsSet;

    /**
     * Creates a Q-value based policy with epsilon-greedy exploration and decay.
     *
     * @param actionsSet      the list of possible actions
     * @param epsilon         the initial exploration rate
     * @param epsilonDecrease the rate at which epsilon decreases
     */
	protected PolicyQValueBased(List<Action> actionsSet, double epsilon, double epsilonDecrease) {
        super(epsilon, epsilonDecrease);
        
        this.actionsSet = actionsSet;
        this.q = new HashMap<>();
    }

    /**
     * Creates a Q-value based policy with epsilon-greedy exploration (no decay).
     *
     * @param actionsSet the list of possible actions
     * @param epsilon    the exploration rate
     */
	protected PolicyQValueBased(List<Action> actionsSet, double epsilon) {
        this(actionsSet, epsilon, 0.0);
    }

    /**
     * Creates a Q-value based policy with default epsilon of 0.05.
     *
     * @param actionsSet the list of possible actions
     */
	protected PolicyQValueBased(List<Action> actionsSet) {
        this(actionsSet, 0.05);
    }
	
	@Override
	public void init(MLKAgent agent) {
		this.q = new HashMap<>();
	}

	@Override
	public MLKAgent getAgent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLearningFrequency() {
		// TODO Auto-generated method stub
		return 0;
	}

    /**
     * Selects an action using epsilon-greedy strategy.
     * <p>
     * With probability epsilon, returns a random action. Otherwise, returns
     * the action with the highest Q-value for the given observation.
     * If a state-action pair has not been visited, that action is preferred
     * to encourage exploration.
     * </p>
     *
     * @param observation the current observation
     * @return the selected action
     */
	@Override
    public Action takeAction(Observation observation) {
		
		RandomGenerator random = pnrg();
        if (random.nextDouble() < getEpsilon()) {
            return actionsSet.get(random.nextInt(actionsSet.size()));
        }

        Action selectedAction = null;
        double maxVal = Double.NEGATIVE_INFINITY;
        for (Action act : actionsSet) {
            Pair<Observation, Action> newStateAction = new Pair<>(observation, act);
            if (!q.containsKey(newStateAction)) {
                return act;
            }

            if (q.get(newStateAction) > maxVal) {
                selectedAction = act;
                maxVal = q.get(newStateAction);
            }
        }

        return selectedAction;
    }

	@Override
	public void learnOnBatch(Batch batch, AgentLogger logger) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endEpisode(Batch batch, AgentLogger logger) {
		// TODO Auto-generated method stub

	}

    /**
     * Returns the Q-table mapping state-action pairs to their values.
     *
     * @return the Q-value map
     */
	public Map<Pair<Observation, Action>, Double> getQ() {
        return q;
    }

    /**
     * Returns the set of possible actions.
     *
     * @return the list of actions
     */
	protected List<Action> getActionsSet(){
        return actionsSet;
    }
	
	
	

}

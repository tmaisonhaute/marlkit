package marlkit.gooryield;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import agent.MLKAgent;
import agent.action.Action;
import environment.EnvironmentStandard;
import environment.observation.Observation;
import environment.state.State;
import marlkit.gooryield.events.MatrixRewardEvent;
import rewardmodeling.ReactionEvent;
import rewardmodels.MixedReward;

/**
 * Two-agent, single-step, single-state matrix game: GoOrYield.
 */
public class EnvGoOrYield extends EnvironmentStandard {

	public static final int OUTCOME_YIELD_YIELD = 0;
	public static final int OUTCOME_YIELD_GO = 1;
	public static final int OUTCOME_GO_YIELD = 2;
	public static final int OUTCOME_GO_GO = 3;

	private static final int ACTION_YIELD = 0;
	private static final int ACTION_GO = 1;

	/**
	 * Payoff matrix indexed by (a0, a1, agentIndex).
	 * a0/a1: 0=Yield, 1=Go. agentIndex: 0=agent0, 1=agent1.
	 */
	private static final double[][][] PAYOFF = {
			// a0 = Yield
			{ { -2.0, -2.0 }, { 0.0, 5.0 } },
			// a0 = Go
			{ { 5.0, 0.0 }, { -10.0, -10.0 } } };

	private static final ObservationGoOrYield OBSERVATION = new ObservationGoOrYield();

	private final long[] outcomeCounts = new long[4];

	public long[] getOutcomeCounts() {
		return outcomeCounts.clone();
	}

	public EnvGoOrYield() {
		super(1, 1, new MixedReward());
	}

	@Override
	protected void setupState() {
		// Single state: nothing to set.
	}

	@Override
	protected void setupAgents() {
		// Per-episode setup is not needed for this experiment.
	}

	@Override
	public void computeObservations() {
		Map<MLKAgent, Observation> observations = new HashMap<>();
		for (MLKAgent agent : agents.getAgents()) {
			observations.put(agent, OBSERVATION);
		}
		setAgentsObservations(observations);
	}

	@Override
	public Map<MLKAgent, List<ReactionEvent>> dynamics(Map<MLKAgent, Action> actions) {
		List<MLKAgent> agentsList = agents.getAgents();
		if (agentsList.size() != 2) {
			throw new IllegalStateException("GoOrYield expects exactly 2 agents, found: " + agentsList.size());
		}

		MLKAgent agent0 = agentsList.get(0);
		MLKAgent agent1 = agentsList.get(1);

		Action action0 = normalizeAction(actions.get(agent0));
		Action action1 = normalizeAction(actions.get(agent1));

		int i0 = actionIndex(action0);
		int i1 = actionIndex(action1);

		int outcomeIndex = outcomeIndex(i0, i1);
		outcomeCounts[outcomeIndex]++;

		double reward0 = PAYOFF[i0][i1][0];
		double reward1 = PAYOFF[i0][i1][1];

		Map<MLKAgent, List<ReactionEvent>> results = new HashMap<>();
		results.put(agent0, new MatrixRewardEvent(reward0).toList());
		results.put(agent1, new MatrixRewardEvent(reward1).toList());
		return results;
	}

	private Action normalizeAction(Action action) {
		if (action instanceof ActionGo || action instanceof ActionYield) {
			return action;
		}
		if (action == null) {
			getLogger().warning("Null action received; defaulting to Yield");
		} else {
			getLogger().log(Level.WARNING, "Unexpected action type {0}; defaulting to Yield", action.getClass().getName());
		}
		return new ActionYield();
	}

	private int actionIndex(Action action) {
		return (action instanceof ActionGo) ? ACTION_GO : ACTION_YIELD;
	}

	private int outcomeIndex(int i0, int i1) {
		// With Yield=0 and Go=1, this matches the OUTCOME_* constants.
		return (i0 * 2) + i1;
	}

	@Override
	public State getState() {
		return new State() {

			@Override
			public void print() {
				System.out.println("GoOrYield (single state)");
			}

			@Override
			public Map<MLKAgent, Observation> getObservations() {
				return getAgentsObservations();
			}

			@Override
			public void reset() {
				// Single state.
			}
		};
	}
}

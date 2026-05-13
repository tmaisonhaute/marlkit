package rewardmodels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import environment.reward.Reward;
import rewardmodeling.ReactionEvent;
import rewardmodeling.RewardModel;

/**
 * Mixed reward model with a fairness term based on the minimum agent reward.
 *
 * <p>For each agent $i$, the final reward is:
 * $$R_i^{final} = (1 - \delta)R_i + \delta R_{min}$$
 * where $R_{min}$ is the minimum reward across all agents for the step.
 * With $\delta = 0$, this behaves like independent rewards; with $\delta = 1$,
 * every agent receives the minimum reward.</p>
 */
public class FairMixedReward implements RewardModel {
	private final double alpha;

	/**
	 * Create a fairness-mixed reward model.
	 *
	 * @param delta Mixing parameter in $[0,1]$.
	 */
	public FairMixedReward(double delta) {
		if (delta < 0.0 || delta > 1.0) {
			throw new IllegalArgumentException("alpha must be in [0, 1].");
		}
		this.alpha = delta;
	}

	/**
	 * Compute mixed rewards using the minimum reward as a fairness anchor.
	 *
	 * @param agentEvents Map of agent to its reaction events.
	 * @return Map of agent to its mixed reward.
	 */
	@Override
	public Map<MLKAgent, Reward> rewardFunctions(Map<MLKAgent, List<ReactionEvent>> agentEvents) {
		Map<MLKAgent, Reward> rewards = new HashMap<>();
		double minValue = Double.POSITIVE_INFINITY;

		for (Map.Entry<MLKAgent, List<ReactionEvent>> entry : agentEvents.entrySet()) {
			Reward reward = computeAgentReward(entry.getValue());
			rewards.put(entry.getKey(), reward);
			minValue = Math.min(minValue, reward.getValue());
		}

		if (rewards.isEmpty()) {
			return rewards;
		}

		for (Map.Entry<MLKAgent, Reward> entry : rewards.entrySet()) {
			Reward reward = entry.getValue();
			double value = reward.getValue();
			reward.setReward(value * (1.0 - alpha) + minValue * alpha);
		}

		return rewards;
	}
}

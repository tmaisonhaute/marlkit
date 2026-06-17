package marlkit.collectingresource.reward;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import marlkit.collectingresource.environment.events.CollectResourceEvent2D;
import reward.ReactionEvent;
import reward.Reward;
import reward.RewardModel;
import reward.RewardStandard;

/**
 * Reward model that scales Trade2D collection rewards based on availability.
 *
 * <p>For each collection event, the base reward is multiplied by
 * $\min(1, \frac{available}{requesters})^{c}$ where $c$ is the coefficient.</p>
 */
public class LogisticalRewardModel implements RewardModel {
	private final double coefficient;

	/**
	 * Create a Trade2D availability reward model.
	 *
	 * @param coefficient Scaling coefficient applied to collection rewards.
	 */
	public LogisticalRewardModel(double coefficient) {
		if (coefficient < 0.0) {
			throw new IllegalArgumentException("coefficient must be positive.");
		}
		this.coefficient = coefficient;
	}

	@Override
	public Map<MLKAgent, Reward> rewardFunctions(Map<MLKAgent, List<ReactionEvent>> agentEvents) {
		Map<MLKAgent, Reward> rewards = new HashMap<>();
		for (Map.Entry<MLKAgent, List<ReactionEvent>> entry : agentEvents.entrySet()) {
			Reward total = new RewardStandard(0.0);
			for (ReactionEvent event : entry.getValue()) {
				Reward eventReward = event.toReward();
				if (event instanceof CollectResourceEvent2D collectEvent) {
					double factor = computeFactor(collectEvent.getAvailableStock(), collectEvent.getRequesterCount());
					eventReward.setReward(eventReward.getValue() * factor);
				}
				total.add(eventReward);
			}
			rewards.put(entry.getKey(), total);
		}
		return rewards;
	}

	/**
	 * Compute the availability scaling factor.
	 *
	 * @param availableStock Stock available before allocation.
	 * @param requesterCount Number of requesters.
	 * @return Scaling factor for collection rewards.
	 */
	private double computeFactor(int availableStock, int requesterCount) {
		if (requesterCount <= 0) {
			return 0.0;
		}
		double ratio = availableStock / (double) requesterCount;
		double capped = Math.min(1.0, ratio);
		return Math.pow(capped, coefficient);
	}
}

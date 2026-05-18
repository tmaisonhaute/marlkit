package rewardmodelimplementation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import reward.ReactionEvent;
import reward.Reward;
import reward.RewardModel;

public class ZeroSumReward implements RewardModel {

	@Override
	public Map<MLKAgent, Reward> rewardFunctions(Map<MLKAgent, List<ReactionEvent>> agentEvents) {
		Map<MLKAgent, Reward> rewards = new HashMap<>();
		double totalValue = 0.0;

		for (Map.Entry<MLKAgent, List<ReactionEvent>> entry : agentEvents.entrySet()) {
			List<ReactionEvent> events = entry.getValue();
			Reward reward = computeAgentReward(events);
			rewards.put(entry.getKey(), reward);
			totalValue += reward.getValue();
		}

		int nbAgents = agentEvents.size();
		if (nbAgents == 0) {
			return rewards;
		}

		double averageReward = totalValue / nbAgents;
		for (Map.Entry<MLKAgent, List<ReactionEvent>> entry : agentEvents.entrySet()) {
			Reward reward = rewards.get(entry.getKey());
			reward.setReward(reward.getValue() - averageReward);
		}

		return rewards;
	}
}
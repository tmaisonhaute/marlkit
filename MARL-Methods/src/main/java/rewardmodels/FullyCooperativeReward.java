package rewardmodels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import environment.reward.Reward;
import rewardmodeling.ReactionEvent;
import rewardmodeling.RewardModel;

public class FullyCooperativeReward implements RewardModel {

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
        for (Map.Entry<MLKAgent, List<ReactionEvent>> entry : agentEvents.entrySet()) {
        	rewards.get(entry.getKey()).setReward(totalValue / nbAgents);
        }
        return rewards;
	}

}

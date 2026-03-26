package marlkit.teamsurround;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import environment.reward.Reward;
import rewardmodeling.ReactionEvent;
import rewardmodeling.RewardModel;

public class RewardModelTeam implements RewardModel {

	@Override
	public Map<MLKAgent, Reward> rewardFunctions(Map<MLKAgent, List<ReactionEvent>> agentEvents) {
		Map<MLKAgent, Reward> rewards = new HashMap<>();
		double team1Total = 0.0;
		double team2Total = 0.0;
		int team1Count = 0;
		int team2Count = 0;

		for (Map.Entry<MLKAgent, List<ReactionEvent>> entry : agentEvents.entrySet()) {
			MLKAgent agent = entry.getKey();
			Reward reward = computeAgentReward(entry.getValue());
			rewards.put(agent, reward);
			if (agent instanceof AgentTeam1) {
				team1Total += reward.getValue();
				team1Count++;
			} else if (agent instanceof AgentTeam2) {
				team2Total += reward.getValue();
				team2Count++;
			}
		}

		double team1Average = team1Count == 0 ? 0.0 : team1Total / team1Count;
		double team2Average = team2Count == 0 ? 0.0 : team2Total / team2Count;

		for (Map.Entry<MLKAgent, Reward> entry : rewards.entrySet()) {
			if (entry.getKey() instanceof AgentTeam1) {
				entry.getValue().setReward(team1Average);
			} else if (entry.getKey() instanceof AgentTeam2) {
				entry.getValue().setReward(team2Average);
			}
		}

		return rewards;
	}
}

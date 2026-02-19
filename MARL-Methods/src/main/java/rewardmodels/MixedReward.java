package rewardmodels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import environment.reward.Reward;
import rewardmodeling.ReactionEvent;
import rewardmodeling.RewardModel;

public class MixedReward implements RewardModel {

    /**
     * Computes independent rewards for each agent based solely on their own events.
     *
     * @param agentEvents map of agent to their list of events
     * @return map of agent to their computed reward
     */
    @Override
    public Map<MLKAgent, Reward> rewardFunctions(Map<MLKAgent, List<ReactionEvent>> agentEvents) {
        Map<MLKAgent, Reward> rewards = new HashMap<>();
        for (Map.Entry<MLKAgent, List<ReactionEvent>> entry : agentEvents.entrySet()) {
            MLKAgent agent = entry.getKey();
            List<ReactionEvent> events = entry.getValue();
            Reward reward = computeAgentReward(events);
            rewards.put(agent, reward);
        }
        return rewards;
    }
}

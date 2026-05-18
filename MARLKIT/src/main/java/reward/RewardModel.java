package reward;

import java.util.List;
import java.util.Map;

import agent.MLKAgent;

public interface RewardModel {
    /**
     * Computes the total reward for an agent based on their events.
     *
     * @param events the list of events for the agent
     * @return the total reward object
     */
    default Reward computeAgentReward(List<ReactionEvent> events) {
    	Reward total = new RewardStandard(0.0);
    	for (ReactionEvent event : events) {
    		total.add(event.toReward());
    	}
    	return total;
    }

	/**
	 * Computes rewards for each agent based on events.
	 * Allow for interactions between agents' events when computing rewards.
	 *
	 * @param agentEvents map of agent to their list of events
	 * @return map of agent to their computed reward
	 */
	Map<MLKAgent, Reward> rewardFunctions(Map<MLKAgent, List<ReactionEvent>> agentEvents);
	
}

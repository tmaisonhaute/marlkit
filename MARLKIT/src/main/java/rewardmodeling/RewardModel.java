package rewardmodeling;

import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import environment.reward.Reward;
import environment.reward.RewardStandard;

public interface RewardModel {
    /**
     * Computes the total reward for an agent based on their events.
     * Subclasses may override for more complex logic, but the default sums event rewards.
     * Useful for multiple events occurring in a single time step.
     *
     * @param events the list of events for the agent
     * @return the total reward object
     */
    default Reward computeAgentReward(List<Event> events) {
    	Reward total = new RewardStandard(0.0);
    	for (Event event : events) {
    		total.add(event.toReward());
    	}
    	return total;
    }

	/**
	 * Computes rewards for each agent based on events.
	 *
	 * @param agentEvents map of agent to their list of events
	 * @return map of agent to their computed reward
	 */
	Map<MLKAgent, Reward> computeRewards(Map<MLKAgent, List<Event>> agentEvents);
	
}

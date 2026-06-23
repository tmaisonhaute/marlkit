package rewardmodelimplementation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import reward.ReactionEvent;
import reward.Reward;
import reward.RewardModel;

/**
 * Semi-cooperative reward model that combines individual and cooperative rewards.
 * The cooperation factor determines the weight of the cooperative component in the final reward. 
 * It must be between 0 and 1, where 0 means fully individualistic ({@link MixedReward}) and 1 means fully cooperative ({@link FullyCooperativeReward}).
 * 
 */
public class SemiCooperativeReward implements RewardModel {
	protected double cooperationFactor;
	
	/**
	 * Constructor for SemiCooperativeReward.
	 * @param cooperationFactor the weight of the cooperative component in the final reward, must be between 0 and 1.
	 * @throws IllegalArgumentException if the cooperation factor is not between 0 and 1.
	 */
	public SemiCooperativeReward(double cooperationFactor) {
		super();
		if (cooperationFactor < 0.0 || cooperationFactor > 1.0) {
			throw new IllegalArgumentException("Cooperation factor must be between 0 and 1.");
		}
		this.cooperationFactor = cooperationFactor;
	}

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
        	double individualReward = rewards.get(entry.getKey()).getValue();
        	double cooperativeReward = totalValue / nbAgents;
        	double semiCooperativeReward = (1 - cooperationFactor) * individualReward + cooperationFactor * cooperativeReward;
        	rewards.get(entry.getKey()).setReward(semiCooperativeReward);
        }
        return rewards;
	}

}

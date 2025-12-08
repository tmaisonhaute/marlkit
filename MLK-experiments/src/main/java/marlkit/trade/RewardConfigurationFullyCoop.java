package marlkit.trade;

import java.util.HashMap;
import java.util.Map;

import agent.MLKAgent;
import environment.reward.Reward;
import environment.reward.RewardStandard;

public class RewardConfigurationFullyCoop extends RewardConfiguration {
    @Override
	public Map<MLKAgent, Reward> computeRewards(Map<MLKAgent, ResourceQuantify> receivedResource, Map<ResourceType, Float> basePrices) {
		Map<MLKAgent, Reward> rewards = new HashMap<>();
        float totalValue = 0f;
        for (ResourceQuantify rq : receivedResource.values()) {
            float basePrice = basePrices.get(rq.getType());
            totalValue += rq.getValue() * basePrice;
        }

        int nbAgents = receivedResource.size();
        for (MLKAgent agent : receivedResource.keySet()) {
            rewards.put(agent, new RewardStandard(totalValue/nbAgents));
        }

        return rewards;
    }
}

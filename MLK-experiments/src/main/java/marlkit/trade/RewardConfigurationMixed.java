package marlkit.trade;

import java.util.HashMap;
import java.util.Map;

import agent.MLKAgent;
import environment.reward.Reward;
import environment.reward.RewardStandard;

public class RewardConfigurationMixed extends RewardConfiguration {

	@Override
	public Map<MLKAgent, Reward> computeRewards(Map<MLKAgent, ResourceQuantify> receivedResource, Map<ResourceType, Float> basePrices) {
		Map<MLKAgent, Reward> rewards = new HashMap<>();
		for(MLKAgent agent : receivedResource.keySet()){
			ResourceQuantify rq = receivedResource.get(agent);
			float basePrice = basePrices.get(rq.getType());
			float rewardValue = rq.getValue() * basePrice;
			Reward reward = new RewardStandard(rewardValue);
			rewards.put(agent, reward);
		}
		return rewards;
	}

}

package marlkit.trade;

import java.util.Map;

import environment.reward.Reward;
import environment.reward.RewardStandard;
import rewardmodeling.ReactionEvent;

public class CollectResourceEvent extends ReactionEvent{
	private double rewardValue;
	

	public CollectResourceEvent(Map<ResourceType, Float> basePrices, ResourceQuantify collectedResource) {
		
		float basePrice = basePrices.get(collectedResource.getType());
		float reward = collectedResource.getValue() * basePrice;
		this.rewardValue = reward;
	}
	
	@Override
	public Reward toReward() {
		return new RewardStandard(rewardValue);
	}

}

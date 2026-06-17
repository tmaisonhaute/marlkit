package marlkit.collectingresource.environment.events;

import java.util.Map;

import marlkit.collectingresource.environment.ResourceQuantify;
import marlkit.collectingresource.environment.ResourceType;
import reward.ReactionEvent;
import reward.Reward;
import reward.RewardStandard;

/**
 * Reaction event for resource collection in Trade2D with allocation context.
 */
public class CollectResourceEvent2D extends ReactionEvent {
	private final double baseReward;
	private final int availableStock;
	private final int requesterCount;

	/**
	 * Create a collect resource event with allocation context.
	 *
	 * @param basePrices Base prices per resource type.
	 * @param collectedResource Collected resource quantity.
	 * @param availableStock Stock available before processing requests.
	 * @param requesterCount Number of agents requesting the unit.
	 */
	public CollectResourceEvent2D(Map<ResourceType, Float> basePrices,
			ResourceQuantify collectedResource,
			int availableStock,
			int requesterCount) {
		float basePrice = basePrices.get(collectedResource.getType());
		this.baseReward = collectedResource.getValue() * basePrice;
		this.availableStock = availableStock;
		this.requesterCount = requesterCount;
	}

	public double getBaseReward() {
		return baseReward;
	}

	public int getAvailableStock() {
		return availableStock;
	}

	public int getRequesterCount() {
		return requesterCount;
	}

	@Override
	public Reward toReward() {
		return new RewardStandard(baseReward);
	}
}

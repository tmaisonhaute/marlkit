package marlkit.foraging.events;

import rewardmodeling.ReactionEventDefault;

public class FoodCollectedEvent extends ReactionEventDefault {
	private static final double REWARD_FOOD_COLLECTED = 10.0;
	
	public FoodCollectedEvent() {
		super(REWARD_FOOD_COLLECTED);
	}
}

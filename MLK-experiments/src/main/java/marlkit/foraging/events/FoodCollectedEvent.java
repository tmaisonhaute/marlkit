package marlkit.foraging.events;

import reward.ReactionEventDefault;

public class FoodCollectedEvent extends ReactionEventDefault {
	private static final double REWARD_FOOD_COLLECTED = 15.0;
	
	public FoodCollectedEvent(double coef) {
		super(REWARD_FOOD_COLLECTED * coef);
	}
}

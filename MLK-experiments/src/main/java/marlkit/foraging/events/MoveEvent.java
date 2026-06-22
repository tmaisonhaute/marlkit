package marlkit.foraging.events;

import reward.ReactionEventDefault;

public class MoveEvent extends ReactionEventDefault {
	private static final double REWARD_MOVE = -0.01;
	
	public MoveEvent() {
		super(REWARD_MOVE);
	}
}

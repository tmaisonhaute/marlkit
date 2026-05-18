package marlkit.pushtheblock.events;

import reward.ReactionEventDefault;

public class MoveEvent extends ReactionEventDefault {
	private static final double REWARD_MOVE = -0.1;
	
	public MoveEvent() {
		super(REWARD_MOVE);
	}
}

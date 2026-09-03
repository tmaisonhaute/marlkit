package marlkit.foraging.events;

import reward.ReactionEventDefault;

public class MoveEvent extends ReactionEventDefault {
	private static final double REWARD_MOVE = -0.1;
	private static final double REWARD_IDLE = -1;
	
	public MoveEvent(boolean hasMoved) {
		super(hasMoved ? REWARD_MOVE : REWARD_IDLE);
	}
}

package marlkit.foraging.events;

import rewardmodeling.ReactionEventDefault;

public class MoveEvent extends ReactionEventDefault {
	private static final double REWARD_MOVE = -0.0;
	
	public MoveEvent() {
		super(REWARD_MOVE);
	}
}

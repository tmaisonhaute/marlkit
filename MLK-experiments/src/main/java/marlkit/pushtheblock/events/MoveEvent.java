package marlkit.pushtheblock.events;

import rewardmodeling.EventDefault;

public class MoveEvent extends EventDefault {
	private static final double REWARD_MOVE = -0.1;
	
	public MoveEvent() {
		super(REWARD_MOVE);
	}
}

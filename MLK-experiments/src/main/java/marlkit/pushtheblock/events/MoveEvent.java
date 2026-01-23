package marlkit.pushtheblock.events;

import rewardmodeling.EventDefault;

public class MoveEvent extends EventDefault {
	private static final double REWARDMOVE = -0.1;
	
	public MoveEvent() {
		super(REWARDMOVE);
	}
}

package marlkit.listenorgo.events;

import rewardmodeling.EventDefault;

public class DoNothingEvent extends EventDefault {
	private static final double REWARD_VALUE = 0.0;

	public DoNothingEvent() {
		super(REWARD_VALUE);
	}

}

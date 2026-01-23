package marlkit.listenorgo.events;

import rewardmodeling.EventDefault;

public class ListenEvent extends EventDefault {
    private static final double REWARD_VALUE = -0.1;

	public ListenEvent() {
		super(REWARD_VALUE);
	}

}

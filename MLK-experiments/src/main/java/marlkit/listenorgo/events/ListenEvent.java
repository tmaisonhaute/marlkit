package marlkit.listenorgo.events;

import rewardmodeling.ReactionEventDefault;

public class ListenEvent extends ReactionEventDefault {
    private static final double REWARD_VALUE = -0.1;

	public ListenEvent() {
		super(REWARD_VALUE);
	}

}

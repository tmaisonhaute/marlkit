package marlkit.listenorgo.events;

import rewardmodeling.ReactionEventDefault;

public class DoNothingEvent extends ReactionEventDefault {
	private static final double REWARD_VALUE = 0.0;

	public DoNothingEvent() {
		super(REWARD_VALUE);
	}

}

package marlkit.crossescape.events;

import rewardmodeling.ReactionEventDefault;

public class NothingEvent extends ReactionEventDefault {
	private static final double REWARD = 0.0;

	public NothingEvent() {
		super(REWARD);
	}
}

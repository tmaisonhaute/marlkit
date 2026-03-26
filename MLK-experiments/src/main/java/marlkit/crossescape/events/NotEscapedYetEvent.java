package marlkit.crossescape.events;

import rewardmodeling.ReactionEventDefault;

public class NotEscapedYetEvent extends ReactionEventDefault {
	private static final double REWARD = -1.0;

	public NotEscapedYetEvent() {
		super(REWARD);
	}
}

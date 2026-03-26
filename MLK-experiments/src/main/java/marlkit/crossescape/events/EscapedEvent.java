package marlkit.crossescape.events;

import rewardmodeling.ReactionEventDefault;

public class EscapedEvent extends ReactionEventDefault {
	private static final double REWARD = 10.0;

	public EscapedEvent() {
		super(REWARD);
	}
}

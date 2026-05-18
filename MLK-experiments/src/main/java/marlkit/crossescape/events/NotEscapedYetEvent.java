package marlkit.crossescape.events;

import reward.ReactionEventDefault;

/**
 * Per-step penalty event emitted while an agent has not escaped yet.
 */
public class NotEscapedYetEvent extends ReactionEventDefault {
	private static final double REWARD = -1.0;

	public NotEscapedYetEvent() {
		super(REWARD);
	}
}

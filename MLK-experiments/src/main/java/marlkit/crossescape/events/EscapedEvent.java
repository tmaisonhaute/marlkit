package marlkit.crossescape.events;

import reward.ReactionEventDefault;

/**
 * Reward event emitted when an agent reaches its target exit.
 */
public class EscapedEvent extends ReactionEventDefault {
	private static final double REWARD = 10.0;

	public EscapedEvent() {
		super(REWARD);
	}
}

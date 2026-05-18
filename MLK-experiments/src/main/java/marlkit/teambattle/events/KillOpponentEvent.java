package marlkit.teambattle.events;

import reward.ReactionEventDefault;

/**
 * Reward event emitted when an agent kills an opposing agent.
 */
public class KillOpponentEvent extends ReactionEventDefault {

	private static final double REWARD = 5.0;

	public KillOpponentEvent() {
		super(REWARD);
	}
}

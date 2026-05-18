package marlkit.teambattle.events;

import reward.ReactionEventDefault;

/**
 * Penalty event emitted when an agent dies.
 */
public class DeadPenaltyEvent extends ReactionEventDefault {

	private static final double REWARD = -0.1;

	public DeadPenaltyEvent() {
		super(REWARD);
	}
}

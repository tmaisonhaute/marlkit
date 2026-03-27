package marlkit.teamsurround.events;

import rewardmodeling.ReactionEventDefault;

/**
 * Penalty event emitted when a TeamSurround agent dies.
 */
public class DeadPenaltyEvent extends ReactionEventDefault {

	private static final double REWARD = -0.1;

	public DeadPenaltyEvent() {
		super(REWARD);
	}
}

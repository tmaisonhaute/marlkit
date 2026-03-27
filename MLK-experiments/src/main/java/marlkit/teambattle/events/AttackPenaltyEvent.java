package marlkit.teambattle.events;

import rewardmodeling.ReactionEventDefault;

/**
 * Penalty event applied when an agent performs an attack action.
 */
public class AttackPenaltyEvent extends ReactionEventDefault {

	private static final double REWARD = -0.1;

	public AttackPenaltyEvent() {
		super(REWARD);
	}
}

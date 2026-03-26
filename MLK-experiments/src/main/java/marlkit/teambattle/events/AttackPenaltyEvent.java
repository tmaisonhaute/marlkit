package marlkit.teambattle.events;

import rewardmodeling.ReactionEventDefault;

public class AttackPenaltyEvent extends ReactionEventDefault {

	private static final double REWARD = -0.1;

	public AttackPenaltyEvent() {
		super(REWARD);
	}
}

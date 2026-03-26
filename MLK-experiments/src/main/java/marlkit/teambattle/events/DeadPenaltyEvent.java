package marlkit.teambattle.events;

import rewardmodeling.ReactionEventDefault;

public class DeadPenaltyEvent extends ReactionEventDefault {

	private static final double REWARD = -0.1;

	public DeadPenaltyEvent() {
		super(REWARD);
	}
}

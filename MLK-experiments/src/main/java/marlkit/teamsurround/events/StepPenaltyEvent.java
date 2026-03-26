package marlkit.teamsurround.events;

import rewardmodeling.ReactionEventDefault;

public class StepPenaltyEvent extends ReactionEventDefault {

	private static final double REWARD = -0.005;

	public StepPenaltyEvent() {
		super(REWARD);
	}
}

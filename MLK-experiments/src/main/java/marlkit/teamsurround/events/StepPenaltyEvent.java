package marlkit.teamsurround.events;

import rewardmodeling.ReactionEventDefault;

/**
 * Per-step penalty event for TeamSurround agents.
 */
public class StepPenaltyEvent extends ReactionEventDefault {

	private static final double REWARD = -0.005;

	public StepPenaltyEvent() {
		super(REWARD);
	}
}

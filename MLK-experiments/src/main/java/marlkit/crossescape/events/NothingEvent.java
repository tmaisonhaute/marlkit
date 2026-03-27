package marlkit.crossescape.events;

import rewardmodeling.ReactionEventDefault;

/**
 * Neutral event emitted for agents that already completed their objective.
 */
public class NothingEvent extends ReactionEventDefault {
	private static final double REWARD = 0.0;

	public NothingEvent() {
		super(REWARD);
	}
}

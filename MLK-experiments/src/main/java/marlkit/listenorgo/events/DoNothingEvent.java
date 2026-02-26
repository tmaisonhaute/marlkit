package marlkit.listenorgo.events;

import rewardmodeling.ReactionEventDefault;

/**
 * Reaction event produced when an agent attempts an action but is already
 * committed to a direction, resulting in no effect and a zero reward.
 */
public class DoNothingEvent extends ReactionEventDefault {
	private static final double REWARD_VALUE = 0.0;

	/**
	 * Creates a new DoNothingEvent with a reward of zero.
	 */
	public DoNothingEvent() {
		super(REWARD_VALUE);
	}

}

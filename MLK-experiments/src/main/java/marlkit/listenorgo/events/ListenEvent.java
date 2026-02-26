package marlkit.listenorgo.events;

import rewardmodeling.ReactionEventDefault;

/**
 * Reaction event produced when an agent performs a {@link marlkit.listenorgo.ActionListen}.
 * <p>
 * A small negative reward is applied to incentivise agents to commit
 * to a direction as early as they are confident enough.
 * </p>
 */
public class ListenEvent extends ReactionEventDefault {
    private static final double REWARD_VALUE = -0.1;

	/**
	 * Creates a new ListenEvent with a reward of {@value #REWARD_VALUE}.
	 */
	public ListenEvent() {
		super(REWARD_VALUE);
	}

}

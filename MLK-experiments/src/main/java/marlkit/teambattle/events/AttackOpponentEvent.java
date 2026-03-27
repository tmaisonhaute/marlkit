package marlkit.teambattle.events;

import rewardmodeling.ReactionEventDefault;

/**
 * Reward event emitted when an attack successfully hits an opponent.
 */
public class AttackOpponentEvent extends ReactionEventDefault {

	private static final double REWARD = 0.2;

	public AttackOpponentEvent() {
		super(REWARD);
	}
}

package marlkit.teambattle.events;

import rewardmodeling.ReactionEventDefault;

public class AttackOpponentEvent extends ReactionEventDefault {

	private static final double REWARD = 0.2;

	public AttackOpponentEvent() {
		super(REWARD);
	}
}

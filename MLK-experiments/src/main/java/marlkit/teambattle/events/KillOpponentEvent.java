package marlkit.teambattle.events;

import rewardmodeling.ReactionEventDefault;

public class KillOpponentEvent extends ReactionEventDefault {

	private static final double REWARD = 5.0;

	public KillOpponentEvent() {
		super(REWARD);
	}
}

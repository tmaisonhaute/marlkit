package marlkit.pushtheblocktogether;

import reward.ReactionEventDefault;

public class TryPushEvent extends ReactionEventDefault {
	private static final double REWARDTRYPUSH = 0.1;

	public TryPushEvent() {
		super(REWARDTRYPUSH);
	}

}

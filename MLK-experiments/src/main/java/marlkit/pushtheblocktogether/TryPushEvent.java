package marlkit.pushtheblocktogether;

import reward.ReactionEventDefault;

public class TryPushEvent extends ReactionEventDefault {
	private static final double REWARDTRYPUSH = 0.0;

	public TryPushEvent() {
		super(REWARDTRYPUSH);
	}

}

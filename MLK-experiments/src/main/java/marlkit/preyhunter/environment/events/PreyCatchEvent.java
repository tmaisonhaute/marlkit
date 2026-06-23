package marlkit.preyhunter.environment.events;

import reward.ReactionEventDefault;

public class PreyCatchEvent extends ReactionEventDefault {
	private static final double REWARDPREYCATCH = 300;

	public PreyCatchEvent() {
		super(REWARDPREYCATCH);
	}

}

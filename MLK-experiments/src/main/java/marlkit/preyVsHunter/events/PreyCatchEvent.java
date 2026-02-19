package marlkit.preyVsHunter.events;

import rewardmodeling.ReactionEventDefault;

public class PreyCatchEvent extends ReactionEventDefault {
	private static final double REWARDPREYCATCH = 100;

	public PreyCatchEvent() {
		super(REWARDPREYCATCH);
	}

}

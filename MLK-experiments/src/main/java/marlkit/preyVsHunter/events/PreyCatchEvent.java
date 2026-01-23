package marlkit.preyVsHunter.events;

import rewardmodeling.EventDefault;

public class PreyCatchEvent extends EventDefault {
	private static final double REWARDPREYCATCH = 100;

	public PreyCatchEvent() {
		super(REWARDPREYCATCH);
	}

}

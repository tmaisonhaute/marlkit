package marlkit.pushtheblock.events;

import rewardmodeling.EventDefault;

public class BlockPushedOutEvent extends EventDefault {
	private static final double REWARDBLOCKPUSHEDOUT = 10;
	
	public BlockPushedOutEvent() {
		super(REWARDBLOCKPUSHEDOUT);
	}
}

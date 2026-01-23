package marlkit.pushtheblock.events;

import rewardmodeling.EventDefault;

public class BlockPushedEvent extends EventDefault {
	private static final double REWARDBLOCKPUSHED = 0.1;
	
	public BlockPushedEvent() {
		super(REWARDBLOCKPUSHED);
	}
}

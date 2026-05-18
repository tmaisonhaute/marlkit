package marlkit.pushtheblock.events;

import reward.ReactionEventDefault;

public class BlockPushedEvent extends ReactionEventDefault {
	private static final double REWARDBLOCKPUSHED = 0.1;
	
	public BlockPushedEvent() {
		super(REWARDBLOCKPUSHED);
	}
}

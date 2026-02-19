package marlkit.pushtheblock.events;

import rewardmodeling.ReactionEventDefault;

public class BlockPushedOutEvent extends ReactionEventDefault {
	private static final double REWARDBLOCKPUSHEDOUT = 10;
	
	public BlockPushedOutEvent() {
		super(REWARDBLOCKPUSHEDOUT);
	}
}

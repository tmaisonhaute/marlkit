package marlkit.listenorgo.events;

import environment.reward.Reward;
import environment.reward.RewardStandard;
import rewardmodeling.Event;

public class MoveEvent extends Event {
	private static final double REWARD_CORRECT = 10.0;
	private static final double REWARD_INCORRECT = -10.0;
	
	private boolean correctDirection;
	
	public MoveEvent(boolean correctDirection) {
		this.correctDirection = correctDirection;
	}
	
	@Override
	public Reward toReward() {
		if (correctDirection) {
			return new RewardStandard(REWARD_CORRECT);
		} else {
			return new RewardStandard(REWARD_INCORRECT);
		}
	}

}

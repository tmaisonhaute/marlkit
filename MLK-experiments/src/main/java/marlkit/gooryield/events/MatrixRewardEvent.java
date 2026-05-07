package marlkit.gooryield.events;

import environment.reward.Reward;
import environment.reward.RewardStandard;
import rewardmodeling.ReactionEvent;

public class MatrixRewardEvent extends ReactionEvent {

	private final double rewardValue;

	public MatrixRewardEvent(double rewardValue) {
		this.rewardValue = rewardValue;
	}

	@Override
	public Reward toReward() {
		return new RewardStandard(rewardValue);
	}
}

package marlkit.gooryield.events;

import reward.ReactionEvent;
import reward.Reward;
import reward.RewardStandard;

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

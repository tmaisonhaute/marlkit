package rewardmodeling;

import environment.reward.Reward;
import environment.reward.RewardStandard;

public class EventDefault extends Event {
	private final double rewardValue;
	
	public EventDefault(double rewardValue) {
		super();
		this.rewardValue = rewardValue;
	}

	@Override
	public Reward toReward() {
		return new RewardStandard(rewardValue);
	}

}

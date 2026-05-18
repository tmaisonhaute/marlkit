package reward;

public class ReactionEventDefault extends ReactionEvent {
	private final double rewardValue;
	
	public ReactionEventDefault(double rewardValue) {
		super();
		this.rewardValue = rewardValue;
	}

	@Override
	public Reward toReward() {
		return new RewardStandard(rewardValue);
	}

}

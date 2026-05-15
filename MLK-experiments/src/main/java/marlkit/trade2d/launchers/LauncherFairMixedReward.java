package marlkit.trade2d.launchers;

import marlkit.trade2d.ScenarioSpatial4;
import marlkit.trade2d.ScenarioTrade2D;
import rewardmodeling.RewardModel;
import rewardmodels.FairMixedReward;

public class LauncherFairMixedReward extends LauncherTrade2D {
	private static final double DEFAULT_DELTA = 0.5;

	private final ScenarioTrade2D scenario;
	private final RewardModel rewardModel;

	public LauncherFairMixedReward() {
		super();
		scenario = new ScenarioSpatial4();
		rewardModel = new FairMixedReward(DEFAULT_DELTA);
	}

	@Override
	protected RewardModel getRewardModel() {
		return this.rewardModel;
	}

	@Override
	protected ScenarioTrade2D getScenario() {
		return this.scenario;
	}

	public static void main(String[] args) {
		executeThisAgent("--agentLogLevel", "INFO", "--start");
	}
}

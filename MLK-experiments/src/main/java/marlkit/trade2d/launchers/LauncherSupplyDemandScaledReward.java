package marlkit.trade2d.launchers;

import marlkit.trade2d.ScenarioSpatial2;
import marlkit.trade2d.ScenarioTrade2D;
import marlkit.trade2d.SupplyDemandScaledRewardModel;
import rewardmodeling.RewardModel;

public class LauncherSupplyDemandScaledReward extends LauncherTrade2D {
	private static final double DEFAULT_COEFFICIENT = 1.0;

	private final ScenarioTrade2D scenario;
	private final RewardModel rewardModel;

	public LauncherSupplyDemandScaledReward() {
		super();
		scenario = new ScenarioSpatial2();
		rewardModel = new SupplyDemandScaledRewardModel(DEFAULT_COEFFICIENT);
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

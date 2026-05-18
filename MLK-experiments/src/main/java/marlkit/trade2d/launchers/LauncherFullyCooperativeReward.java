package marlkit.trade2d.launchers;

import marlkit.trade2d.ScenarioSpatial5;
import marlkit.trade2d.ScenarioTrade2D;
import rewardmodeling.RewardModel;
import rewardmodels.FullyCooperativeReward;

public class LauncherFullyCooperativeReward extends LauncherTrade2D {
	private final ScenarioTrade2D scenario;
	private final RewardModel rewardModel;

	public LauncherFullyCooperativeReward() {
		super();
		scenario = new ScenarioSpatial5();
		rewardModel = new FullyCooperativeReward();
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

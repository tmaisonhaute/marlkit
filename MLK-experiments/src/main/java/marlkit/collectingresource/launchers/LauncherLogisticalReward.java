package marlkit.collectingresource.launchers;

import marlkit.collectingresource.reward.LogisticalRewardModel;
import marlkit.collectingresource.scenario.ScenarioCollectingResource;
import marlkit.collectingresource.scenario.ScenarioSpatial5;
import reward.RewardModel;

public class LauncherLogisticalReward extends LauncherCollectingResource {
	private static final double DEFAULT_COEFFICIENT = 1.0;

	private final ScenarioCollectingResource scenario;
	private final RewardModel rewardModel;

	public LauncherLogisticalReward() {
		super();
		scenario = new ScenarioSpatial5();
		rewardModel = new LogisticalRewardModel(DEFAULT_COEFFICIENT);
	}

	@Override
	protected RewardModel getRewardModel() {
		return this.rewardModel;
	}

	@Override
	protected ScenarioCollectingResource getScenario() {
		return this.scenario;
	}

	public static void main(String[] args) {
		executeThisAgent("--agentLogLevel", "INFO", "--start");
	}
}

package marlkit.collectingresource.launchers;

import marlkit.collectingresource.LogisticalRewardModel;
import marlkit.collectingresource.ScenarioSpatial5;
import reward.RewardModel;
import marlkit.collectingresource.ScenarioCollectingResource;

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

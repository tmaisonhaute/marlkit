package marlkit.collectingresource.launchers;

import marlkit.collectingresource.scenario.ScenarioCollectingResource;
import marlkit.collectingresource.scenario.ScenarioSpatial5;
import reward.RewardModel;
import rewardmodelimplementation.FullyCooperativeReward;

public class LauncherFullyCooperativeReward extends LauncherCollectingResource {
	private final ScenarioCollectingResource scenario;
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
	protected ScenarioCollectingResource getScenario() {
		return this.scenario;
	}

	public static void main(String[] args) {
		executeThisAgent("--agentLogLevel", "INFO", "--start");
	}
}

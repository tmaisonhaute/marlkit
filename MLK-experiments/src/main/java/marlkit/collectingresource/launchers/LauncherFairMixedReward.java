package marlkit.collectingresource.launchers;

import marlkit.collectingresource.scenario.ScenarioCollectingResource;
import marlkit.collectingresource.scenario.ScenarioSpatial5;
import reward.RewardModel;
import rewardmodelimplementation.FairMixedReward;

public class LauncherFairMixedReward extends LauncherCollectingResource {
	private static final double DEFAULT_DELTA = 0.5;

	private final ScenarioCollectingResource scenario;
	private final RewardModel rewardModel;

	public LauncherFairMixedReward() {
		super();
		scenario = new ScenarioSpatial5();
		rewardModel = new FairMixedReward(DEFAULT_DELTA);
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

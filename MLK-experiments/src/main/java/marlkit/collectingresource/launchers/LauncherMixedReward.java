package marlkit.collectingresource.launchers;

import marlkit.collectingresource.scenario.ScenarioCollectingResource;
import marlkit.collectingresource.scenario.ScenarioSpatial5;
import reward.RewardModel;
import rewardmodelimplementation.MixedReward;

public class LauncherMixedReward extends LauncherCollectingResource {
	private ScenarioCollectingResource scenario;
	private RewardModel rewardModel;
	
	public LauncherMixedReward() {
		super();
		scenario = new ScenarioSpatial5();
		rewardModel = new MixedReward();
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

package marlkit.trade2d.launchers;

import marlkit.trade2d.ScenarioSpatial2;
import marlkit.trade2d.ScenarioTrade2D;
import rewardmodeling.RewardModel;
import rewardmodels.MixedReward;

public class LauncherMixedReward extends LauncherTrade2D {
	private ScenarioTrade2D scenario;
	private RewardModel rewardModel;
	
	public LauncherMixedReward() {
		super();
		scenario = new ScenarioSpatial2();
		rewardModel = new MixedReward();
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

package marlkit.foraging.launchers;

import marlkit.foraging.scenario.ScenarioDeterministic1;
import rewardmodelimplementation.FullyCooperativeReward;

/**
 * Launches the Foraging experiment with a fully cooperative reward model.
 */
public class LauncherForagingFullyCooperative extends LauncherForaging {

	public LauncherForagingFullyCooperative() {
		super(new ScenarioDeterministic1(), new FullyCooperativeReward());
	}

	public static void main(String[] args) {
		executeThisAgent("--agentLogLevel"
				, "INFO"
//				,"--noLog"
				, "--start"
//				,"--viewers",MyViewer.class.getName()
		);
	}

}

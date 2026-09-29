package marlkit.foraging.launchers;

import madkit.simulation.EngineAgents;
import marlkit.foraging.SchedulerForaging;
import marlkit.foraging.ViewerForaging;
import marlkit.foraging.scenario.ScenarioDeterministic1;
import rewardmodelimplementation.MixedReward;
import simulation.LauncherMetadata;
import simulation.MLKModel;


@EngineAgents(scheduler = SchedulerForaging.class, model = MLKModel.class, viewers = {
		ViewerForaging.class })
@LauncherMetadata(
		title = "Mixed Reward",
		documentationAnchor = "mixed-reward")
public class LauncherForagingMixedReward extends LauncherForaging {

	public LauncherForagingMixedReward() {
		super(new ScenarioDeterministic1(), new MixedReward());
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

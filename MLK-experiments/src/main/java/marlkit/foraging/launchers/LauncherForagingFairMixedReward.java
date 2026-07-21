package marlkit.foraging.launchers;

import madkit.simulation.EngineAgents;
import marlkit.foraging.SchedulerForaging;
import marlkit.foraging.ViewerForaging;
import marlkit.foraging.scenario.ScenarioDeterministic1;
import rewardmodelimplementation.FairMixedReward;
import simulation.MLKModel;

@EngineAgents(scheduler = SchedulerForaging.class, model = MLKModel.class, viewers = {
		ViewerForaging.class })
public class LauncherForagingFairMixedReward extends LauncherForaging {

	public LauncherForagingFairMixedReward() {
		super(new ScenarioDeterministic1(), new FairMixedReward(0.5));
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
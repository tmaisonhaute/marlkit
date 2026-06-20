package marlkit.foragingcontinuously.launchers;

import madkit.simulation.EngineAgents;
import marlkit.foraging.SchedulerForaging;
import marlkit.foraging.ViewerForaging;
import marlkit.foragingcontinuously.agents.AgentForaging;
import marlkit.foragingcontinuously.agents.AgentForagingBroadcastObservation;
import simulation.MLKModel;

@EngineAgents(scheduler = SchedulerForaging.class, model = MLKModel.class, viewers = {
		ViewerForaging.class })
public class LauncherBroadcastObservation extends LauncherConfig {

	@Override
	protected AgentForaging createAgent() {
		return new AgentForagingBroadcastObservation();
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

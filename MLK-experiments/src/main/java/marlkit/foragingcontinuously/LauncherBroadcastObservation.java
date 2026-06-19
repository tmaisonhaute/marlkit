package marlkit.foragingcontinuously;

import agent.AgentStandard;
import madkit.simulation.EngineAgents;
import madkit.simulation.SimuEnvironment;
import marlkit.foraging.EnvForaging;
import marlkit.foraging.SchedulerForaging;
import marlkit.foraging.ViewerForaging;
import marlkit.foragingcontinuously.agents.AgentForagingBroadcastObservation;
import marlkit.foragingcontinuously.scenario.ScenarioContinuously;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(scheduler = SchedulerForaging.class, model = MLKModel.class, viewers = {
		ViewerForaging.class })
public class LauncherBroadcastObservation extends MLKLauncher {

	@SuppressWarnings("unchecked")
	@Override
	protected <E extends SimuEnvironment> E onLaunchEnvironment() {
		EnvForaging env = new EnvForagingContinuously(5, 5, new ScenarioContinuously(15, 2));
		launchAgent(env, Integer.MAX_VALUE);
		return (E) env;
	}
	
	@Override
	protected void onLaunchSimulatedAgents() {
		int nbAgents = 2;
		
		for (int i = 0; i < nbAgents; i++) {			

			AgentStandard ag = new AgentForagingBroadcastObservation();
			launchAgent(ag);
		}
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

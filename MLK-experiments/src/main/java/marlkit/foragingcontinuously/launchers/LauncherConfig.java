package marlkit.foragingcontinuously.launchers;

import madkit.simulation.EngineAgents;
import madkit.simulation.SimuEnvironment;
import marlkit.foraging.EnvForaging;
import marlkit.foraging.SchedulerForaging;
import marlkit.foraging.ViewerForaging;
import marlkit.foragingcontinuously.EnvForagingContinuously;
import marlkit.foragingcontinuously.agents.AgentForaging;
import marlkit.foragingcontinuously.scenario.ScenarioContinuously;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(scheduler = SchedulerForaging.class, model = MLKModel.class, viewers = {
		ViewerForaging.class })
public abstract class LauncherConfig extends MLKLauncher {
	int nbAgents = 2;

	@SuppressWarnings("unchecked")
	@Override
	protected <E extends SimuEnvironment> E onLaunchEnvironment() {
		EnvForaging env = new EnvForagingContinuously(6, 6, new ScenarioContinuously(1), 2, 3);
		launchAgent(env, Integer.MAX_VALUE);
		return (E) env;
	}
	
	@Override
	protected void onLaunchSimulatedAgents() {
		for (int i = 0; i < nbAgents; i++) {
			AgentForaging ag = createAgent();
			launchAgent(ag);
		}
	}
	
	protected abstract AgentForaging createAgent();
	
}

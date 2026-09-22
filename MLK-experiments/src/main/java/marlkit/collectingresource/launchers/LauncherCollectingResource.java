package marlkit.collectingresource.launchers;

import java.util.List;

import madkit.simulation.EngineAgents;
import madkit.simulation.SimuEnvironment;
import marlkit.collectingresource.agent.CollectingResourceAgent;
import marlkit.collectingresource.environment.EnvCollectingResource;
import marlkit.collectingresource.environment.StateUnites2D;
import marlkit.collectingresource.environment.UniteProductionSpatial;
import marlkit.collectingresource.scenario.ScenarioCollectingResource;
import marlkit.collectingresource.scheduler.SchedulerCollectingResource;
import marlkit.collectingresource.viewer.ViewerCollectingResource;
import reward.RewardModel;
import simulation.MLKLauncher;
import simulation.MLKModel;

/**
 * Launcher for the Trade2D experiment.
 */
@EngineAgents(scheduler = SchedulerCollectingResource.class, model = MLKModel.class, viewers = {ViewerCollectingResource.class })
public abstract class LauncherCollectingResource extends MLKLauncher {

	/**
	 * Create and launch the Trade2D environment.
	 *
	 * @param <E> Environment type.
	 * @return Created environment.
	 */
	@Override
	protected <E extends SimuEnvironment> E onLaunchEnvironment() {
		EnvCollectingResource env = new EnvCollectingResource(10, 10, getRewardModel(), getScenario());
		launchAgent(env, Integer.MAX_VALUE);
		return (E) env;
	}

	/**
	 * Create and launch learning agents for the environment.
	 */
	@Override
	protected void onLaunchSimulatedAgents() {
		EnvCollectingResource env = getEnvironment();
		StateUnites2D state = (StateUnites2D) env.getState();
		List<UniteProductionSpatial> unites = state.getUnitesProductions();


		int nbAgents = getScenario().getNumberOfAgents();
		for (int i = 0; i < nbAgents; i++) {
			CollectingResourceAgent ag = new CollectingResourceAgent(unites);
			launchAgent(ag);
		}
	}
	
	protected abstract RewardModel getRewardModel();
	protected abstract ScenarioCollectingResource getScenario();
}

package marlkit.trade2d.launchers;

import java.util.List;

import madkit.simulation.EngineAgents;
import madkit.simulation.SimuEnvironment;
import marlkit.trade2d.EnvTrade2D;
import marlkit.trade2d.ScenarioTrade2D;
import marlkit.trade2d.SchedulerTrade2D;
import marlkit.trade2d.StateUnites2D;
import marlkit.trade2d.Trade2DAgent;
import marlkit.trade2d.UniteProductionSpatial;
import marlkit.trade2d.ViewerTrade2D;
import rewardmodeling.RewardModel;
import simulation.MLKLauncher;
import simulation.MLKModel;

/**
 * Launcher for the Trade2D experiment.
 */
@EngineAgents(scheduler = SchedulerTrade2D.class, model = MLKModel.class, viewers = {
		ViewerTrade2D.class })
public abstract class LauncherTrade2D extends MLKLauncher {

	/**
	 * Create and launch the Trade2D environment.
	 *
	 * @param <E> Environment type.
	 * @return Created environment.
	 */
	@Override
	protected <E extends SimuEnvironment> E onLaunchEnvironment() {
		EnvTrade2D env = new EnvTrade2D(10, 10, getRewardModel(), getScenario());
		launchAgent(env, Integer.MAX_VALUE);
		return (E) env;
	}

	/**
	 * Create and launch learning agents for the environment.
	 */
	@Override
	protected void onLaunchSimulatedAgents() {
		EnvTrade2D env = getEnvironment();
		StateUnites2D state = (StateUnites2D) env.getState();
		List<UniteProductionSpatial> unites = state.getUnitesProductions();


		int nbAgents = getScenario().getNumberOfAgents();
		for (int i = 0; i < nbAgents; i++) {
			Trade2DAgent ag = new Trade2DAgent(unites);
			launchAgent(ag);
		}
	}
	
	protected abstract RewardModel getRewardModel();
	protected abstract ScenarioTrade2D getScenario();
}

package marlkit.trade2d;

import java.util.List;

import madkit.simulation.EngineAgents;
import madkit.simulation.SimuEnvironment;
import rewardmodels.MixedReward;
import simulation.MLKLauncher;
import simulation.MLKModel;

/**
 * Launcher for the Trade2D experiment.
 */
@EngineAgents(scheduler = SchedulerTrade2D.class, model = MLKModel.class, viewers = {
		ViewerTrade2D.class })
public class LauncherTrade2D extends MLKLauncher {
	ScenarioTrade2D scenario = new ScenarioSpatial2();

	/**
	 * Create and launch the Trade2D environment.
	 *
	 * @param <E> Environment type.
	 * @return Created environment.
	 */
	@Override
	protected <E extends SimuEnvironment> E onLaunchEnvironment() {
		EnvTrade2D env = new EnvTrade2D(10, 10, new MixedReward(), this.scenario);
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


		int nbAgents = this.scenario.getNumberOfAgents();
		for (int i = 0; i < nbAgents; i++) {
			Trade2DAgent ag = new Trade2DAgent(unites);
			launchAgent(ag);
		}
	}

	/**
	 * Entry point to run the Trade2D experiment.
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
		executeThisAgent("--agentLogLevel", "INFO", "--start");
	}
}

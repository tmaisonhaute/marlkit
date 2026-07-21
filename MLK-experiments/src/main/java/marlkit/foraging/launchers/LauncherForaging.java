package marlkit.foraging.launchers;

import madkit.simulation.EngineAgents;
import madkit.simulation.SimuEnvironment;
import marlkit.foraging.EnvForaging;
import marlkit.foraging.SchedulerForaging;
import marlkit.foraging.ViewerForaging;
import marlkit.foraging.agent.ForagingAgent;
import marlkit.foraging.scenario.Scenario;
import reward.RewardModel;
import simulation.MLKLauncher;
import simulation.MLKModel;

/**
 * Base launcher for the Foraging experiment.
 */
@EngineAgents(scheduler = SchedulerForaging.class, model = MLKModel.class, viewers = { ViewerForaging.class })
public abstract class LauncherForaging extends MLKLauncher {

	private final Scenario scenario;
	private final RewardModel rewardModel;

	protected LauncherForaging(Scenario scenario, RewardModel rewardModel) {
		super();
		this.scenario = scenario;
		this.rewardModel = rewardModel;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <E extends SimuEnvironment> E onLaunchEnvironment() {
		EnvForaging env = new EnvForaging(5, 6, scenario, rewardModel);
		launchAgent(env, Integer.MAX_VALUE);
		return (E) env;
	}

	@Override
	protected void onLaunchSimulatedAgents() {
		int nbAgents = 2;

		for (int i = 0; i < nbAgents; i++) {
			ForagingAgent agent = new ForagingAgent();
			launchAgent(agent);
		}
	}

	protected RewardModel getRewardModel() {
		return rewardModel;
	}

	protected Scenario getScenario() {
		return scenario;
	}
}
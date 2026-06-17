package marlkit.collectingresource.launchers;

import java.util.ArrayList;
import java.util.List;

import agent.MLKAgent;
import marlkit.collectingresource.agent.CollectingResourceJALAgent;
import marlkit.collectingresource.environment.EnvCollectingResource;
import marlkit.collectingresource.environment.StateUnites2D;
import marlkit.collectingresource.environment.UniteProductionSpatial;

/**
 * Launcher for Trade2D with joint-action learning agents.
 */
public abstract class LauncherCollectingResourceJAL extends LauncherCollectingResource {

	@Override
	protected void onLaunchSimulatedAgents() {
		EnvCollectingResource env = getEnvironment();
		StateUnites2D state = (StateUnites2D) env.getState();
		List<UniteProductionSpatial> unites = state.getUnitesProductions();

		int nbAgents = getScenario().getNumberOfAgents();
		int windowSize = getWindowSize();
		List<CollectingResourceJALAgent> agents = new ArrayList<>(nbAgents);
		for (int i = 0; i < nbAgents; i++) {
			CollectingResourceJALAgent agent = new CollectingResourceJALAgent(unites, windowSize);
			launchAgent(agent);
			agents.add(agent);
		}
		List<MLKAgent> modeledAgents = new ArrayList<>(agents);
		for (CollectingResourceJALAgent agent : agents) {
			agent.initModels(modeledAgents);
		}
	}

	protected int getWindowSize() {
		return 1000;
	}
}

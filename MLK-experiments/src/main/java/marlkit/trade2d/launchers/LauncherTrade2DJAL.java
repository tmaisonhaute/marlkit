package marlkit.trade2d.launchers;

import java.util.ArrayList;
import java.util.List;

import agent.MLKAgent;
import marlkit.trade2d.EnvTrade2D;
import marlkit.trade2d.StateUnites2D;
import marlkit.trade2d.Trade2DJALAgent;
import marlkit.trade2d.UniteProductionSpatial;

/**
 * Launcher for Trade2D with joint-action learning agents.
 */
public abstract class LauncherTrade2DJAL extends LauncherTrade2D {

	@Override
	protected void onLaunchSimulatedAgents() {
		EnvTrade2D env = getEnvironment();
		StateUnites2D state = (StateUnites2D) env.getState();
		List<UniteProductionSpatial> unites = state.getUnitesProductions();

		int nbAgents = getScenario().getNumberOfAgents();
		int windowSize = getWindowSize();
		List<Trade2DJALAgent> agents = new ArrayList<>(nbAgents);
		for (int i = 0; i < nbAgents; i++) {
			Trade2DJALAgent agent = new Trade2DJALAgent(unites, windowSize);
			launchAgent(agent);
			agents.add(agent);
		}
		List<MLKAgent> modeledAgents = new ArrayList<>(agents);
		for (Trade2DJALAgent agent : agents) {
			agent.initModels(modeledAgents);
		}
	}

	protected abstract int getWindowSize();
}

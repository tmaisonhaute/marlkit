package marlkit.gooryield;

import agent.AgentStandard;
import madkit.simulation.EngineAgents;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(
		scheduler = SchedulerGoOrYield.class,
		environment = EnvGoOrYield.class,
		model = MLKModel.class,
		viewers = { ViewerGoOrYield.class })
public class LauncherGoOrYield extends MLKLauncher {

	@Override
	protected void onLaunchSimulatedAgents() {

		int nbAgents = 2;
		for (int i = 0; i < nbAgents; i++) {
			AgentStandard agent = new AgentGoOrYieldMinMax();
			launchAgent(agent);
		}
	}

	public static void main(String[] args) {
		executeThisAgent(
				"--agentLogLevel","INFO",
				"--start");
	}
}

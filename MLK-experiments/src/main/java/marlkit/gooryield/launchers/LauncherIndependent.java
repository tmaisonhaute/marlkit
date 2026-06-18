package marlkit.gooryield.launchers;

import agent.AgentStandard;
import madkit.simulation.EngineAgents;
import marlkit.gooryield.SchedulerGoOrYield;
import marlkit.gooryield.ViewerGoOrYield;
import marlkit.gooryield.agent.AgentGoOrYield;
import marlkit.gooryield.environment.EnvGoOrYield;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(
		scheduler = SchedulerGoOrYield.class,
		environment = EnvGoOrYield.class,
		model = MLKModel.class,
		viewers = { ViewerGoOrYield.class })
public class LauncherIndependent extends MLKLauncher {

	@Override
	protected void onLaunchSimulatedAgents() {

	    int nbAgents = 2;
		for (int i = 0; i < nbAgents; i++) {
			AgentStandard agent = new AgentGoOrYield();
			launchAgent(agent);
		}
	}

	public static void main(String[] args) {
		executeThisAgent(
				"--agentLogLevel","INFO",
				"--start");
	}
}

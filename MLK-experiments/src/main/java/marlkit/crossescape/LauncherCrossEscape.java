package marlkit.crossescape;

import madkit.simulation.EngineAgents;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(scheduler = SchedulerCrossEscape.class, environment = EnvCrossEscape.class, model = MLKModel.class, viewers = {
		ViewerCrossEscape.class })
public class LauncherCrossEscape extends MLKLauncher {

	@Override
	protected void onLaunchSimulatedAgents() {
		for (int i = 0; i < 4; i++) {
			launchAgent(new AgentCrossEscapeQLearning());
		}
	}

	public static void main(String[] args) {
		executeThisAgent("--agentLogLevel", "INFO", "--start");
	}
}

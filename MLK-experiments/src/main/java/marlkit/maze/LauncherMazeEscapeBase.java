package marlkit.maze;

import madkit.simulation.SimuEnvironment;
import simulation.MLKLauncher;

public abstract class LauncherMazeEscapeBase extends MLKLauncher {

	@Override
	@SuppressWarnings("unchecked")
	protected <E extends SimuEnvironment> E onLaunchEnvironment() {
		EnvMazeEscape env = new EnvMazeEscape(getRewardConfig());
		launchAgent(env, Integer.MAX_VALUE);
		return (E) env;
	}

	@Override
	protected void onLaunchSimulatedAgents() {
		launchAgent(new AgentMazeEscapeQLearning());
	}

	protected abstract MazeRewardConfig getRewardConfig();

	protected static void runMazeSimulation() {
		executeThisAgent("--agentLogLevel", "INFO", "--start");
	}
}

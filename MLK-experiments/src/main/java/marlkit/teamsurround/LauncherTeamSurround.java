package marlkit.teamsurround;

import madkit.simulation.EngineAgents;
import madkit.simulation.SimuEnvironment;
import simulation.MLKLauncher;
import simulation.MLKModel;

/**
 * Launcher for the deterministic TeamSurround setup.
 */
@EngineAgents(scheduler = SchedulerTeamSurroundCentralizedExperiences.class, model = MLKModel.class, viewers = {
		ViewerTeamSurround.class })
public class LauncherTeamSurround extends MLKLauncher {

	@Override
	@SuppressWarnings("unchecked")
	protected <E extends SimuEnvironment> E onLaunchEnvironment() {
		EnvTeamSurround env = new EnvTeamSurround();
		launchAgent(env, Integer.MAX_VALUE);
		return (E) env;
	}

	@Override
	protected void onLaunchSimulatedAgents() {
		for (int i = 0; i < EnvTeamSurround.DEFAULT_TEAM_SIZE; i++) {
			launchAgent(new AgentTeam1());
		}
		for (int i = 0; i < EnvTeamSurround.DEFAULT_TEAM_SIZE; i++) {
			launchAgent(new AgentTeam2());
		}
	}

	public static void main(String[] args) {
		executeThisAgent("--agentLogLevel", "INFO", "--start");
	}
}

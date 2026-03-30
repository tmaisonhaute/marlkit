package marlkit.teambattle;

import madkit.simulation.EngineAgents;
import simulation.MLKLauncher;
import simulation.MLKModel;

/**
 * Launcher for the default TeamBattle experiment setup.
 */
@EngineAgents(scheduler = SchedulerTeamBattleCentralizedExperiences.class, environment = EnvTeamBattle.class, model = MLKModel.class, viewers = {
		ViewerTeamBattle.class })
public class LauncherTeamBattle extends MLKLauncher {

	@Override
	protected void onLaunchSimulatedAgents() {
		for (int i = 0; i < EnvTeamBattle.DEFAULT_TEAM_SIZE; i++) {
			launchAgent(new AgentTeamBattleQLearningTeamA());
		}
		for (int i = 0; i < EnvTeamBattle.DEFAULT_TEAM_SIZE; i++) {
			launchAgent(new AgentTeamBattleQLearningTeamB());
		}
	}

	public static void main(String[] args) {
		executeThisAgent("--agentLogLevel", "INFO", "--start");
	}
}

package marlkit.teambattle;

import madkit.simulation.EngineAgents;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(scheduler = SchedulerTeamBattle.class, environment = EnvTeamBattle.class, model = MLKModel.class, viewers = {
		ViewerTeamBattle.class })
public class LauncherTeamBattle extends MLKLauncher {

	@Override
	protected void onLaunchSimulatedAgents() {
		for (int i = 0; i < EnvTeamBattle.DEFAULT_TEAM_SIZE * 2; i++) {
			launchAgent(new AgentTeamBattleQLearning());
		}
	}

	public static void main(String[] args) {
		executeThisAgent("--agentLogLevel", "INFO", "--start");
	}
}

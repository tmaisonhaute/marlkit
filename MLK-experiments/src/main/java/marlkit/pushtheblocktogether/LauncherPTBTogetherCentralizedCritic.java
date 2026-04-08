package marlkit.pushtheblocktogether;

import madkit.simulation.EngineAgents;
import marlkit.pushtheblock.ViewerPTB;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(scheduler = SchedulerCentralizedCriticPTB.class, environment = EnvPushTheBlockTogether.class, model = MLKModel.class, viewers = {
		ViewerPTB.class })
public class LauncherPTBTogetherCentralizedCritic extends MLKLauncher {

	@Override
	protected void onLaunchSimulatedAgents() {
		int nbAgents = 2;
		for (int i = 0; i < nbAgents; i++) {
			AgentPTBTogetherTDActorCriticCentralized ag = new AgentPTBTogetherTDActorCriticCentralized();
			launchAgent(ag);
		}
	}

	public static void main(String[] args) {
		executeThisAgent("--agentLogLevel", "INFO"
//				,"--noLog"
				, "--start"
//				,"--viewers",MyViewer.class.getName()
		);
	}

}

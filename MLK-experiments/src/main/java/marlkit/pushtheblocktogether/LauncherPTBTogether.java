package marlkit.pushtheblocktogether;

import madkit.simulation.EngineAgents;
import marlkit.pushtheblock.SchedulerPTB;
import marlkit.pushtheblock.ViewerPTB;
import marlkit.pushtheblock.agent.AgentPTBqLearning;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(scheduler = SchedulerPTB.class, environment = EnvPushTheBlockTogether.class, model = MLKModel.class, viewers = {
		ViewerPTB.class })
public class LauncherPTBTogether extends MLKLauncher {

	@Override
	protected void onLaunchSimulatedAgents() {
		int nbAgents = 2;
		for (int i = 0; i < nbAgents; i++) {
			AgentPTBqLearning ag = new AgentPTBqLearning();
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

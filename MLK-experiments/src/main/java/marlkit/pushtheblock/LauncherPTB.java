package marlkit.pushtheblock;


import madkit.simulation.EngineAgents;
import simulation.MLKLauncher;
import simulation.MLKModel;


@EngineAgents(scheduler = SchedulerPTB.class, environment = EnvPushTheBlock.class, model = MLKModel.class, viewers = {
		ViewerPTB.class })
public class LauncherPTB extends MLKLauncher {

	@Override
	protected void onLaunchSimulatedAgents() {
		int nbAgents = 1;
		
		for (int i = 0; i < nbAgents; i++) {

//			AgentPTBReinforce ag = new AgentPTBReinforce();
			AgentPTBTDActorCritic ag = new AgentPTBTDActorCritic();
//			AgentPTBqLearning ag = new AgentPTBqLearning();
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




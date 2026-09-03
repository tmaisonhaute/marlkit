package marlkit.pushtheblock;


import madkit.simulation.EngineAgents;
import madkit.simulation.SimuEnvironment;
import marlkit.pushtheblock.agent.AgentPTBqLearning;
import rewardmodelimplementation.MixedReward;
import simulation.MLKLauncher;
import simulation.MLKModel;


@EngineAgents(scheduler = SchedulerPTB.class, model = MLKModel.class, viewers = {
		ViewerPTB.class })
public class LauncherPTB extends MLKLauncher {

	@Override
	protected void onLaunchSimulatedAgents() {
		int nbAgents = 1;
		
		for (int i = 0; i < nbAgents; i++) {

//			AgentPTBReinforce ag = new AgentPTBReinforce();
//			AgentPTBTDActorCritic ag = new AgentPTBTDActorCritic();
			AgentPTBqLearning ag = new AgentPTBqLearning();
			launchAgent(ag);
		}
	}
	
	/**
     * Create and launch the environment.
     *
     * @param <E> Environment type.
     * @return Created environment.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected <E extends SimuEnvironment> E onLaunchEnvironment() {
    	EnvPushTheBlock env = new EnvPushTheBlock(new MixedReward());
        
        launchAgent(env, Integer.MAX_VALUE);
        
        return (E) env;
    }

	public static void main(String[] args) {
		executeThisAgent("--agentLogLevel", "INFO"
//				,"--noLog"
				, "--start"
//				,"--viewers",MyViewer.class.getName()
		);
	}

}




package marlkit.pushtheblocktogether;

import madkit.simulation.EngineAgents;
import madkit.simulation.SimuEnvironment;
import marlkit.pushtheblock.SchedulerPTB;
import marlkit.pushtheblock.ViewerPTB;
import marlkit.pushtheblock.agent.AgentPTBqLearning;
import rewardmodelimplementation.MixedReward;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(scheduler = SchedulerPTB.class, model = MLKModel.class, viewers = {
		ViewerPTB.class })
public class LauncherPTBTogether extends MLKLauncher {

	/**
     * Create and launch the environment.
     *
     * @param <E> Environment type.
     * @return Created environment.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected <E extends SimuEnvironment> E onLaunchEnvironment() {
    	EnvPushTheBlockTogether env = new EnvPushTheBlockTogether(new MixedReward());
        
        launchAgent(env, Integer.MAX_VALUE);
        
        return (E) env;
    }
	
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

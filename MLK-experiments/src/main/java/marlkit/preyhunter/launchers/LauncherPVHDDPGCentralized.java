package marlkit.preyhunter.launchers;

import experience.TransitionExperienceBuilder;
import madkit.simulation.EngineAgents;
import marlkit.preyhunter.agent.HunterAgentDDPGCentralizedCritic;
import marlkit.preyhunter.environment.EnvPreyVsHunter;
import marlkit.preyhunter.scheduler.SchedulerPVHCentralizedCritic;
import marlkit.preyhunter.viewer.ViewerPVH;
import simulation.MLKModel;

@EngineAgents(
        scheduler = SchedulerPVHCentralizedCritic.class,
        model = MLKModel.class,
        viewers = { ViewerPVH.class }
)
public class LauncherPVHDDPGCentralized extends LauncherPVH {

	@Override
	protected void configureEnvironment(EnvPreyVsHunter environment) {
		super.configureEnvironment(environment);
		environment.setExperienceBuilder(new TransitionExperienceBuilder());
	}
	
	@Override
	protected void launchHunters() {
		int maxVisibleHunters = HUNTERS_OBSERVE_OTHER_HUNTERS ? NB_HUNTER_AGENTS - 1 : 0;

        int maxVisiblePreys = NB_PREY_AGENTS;

        for (int i = 0; i < NB_HUNTER_AGENTS; i++) {
        	HunterAgentDDPGCentralizedCritic hunter = new HunterAgentDDPGCentralizedCritic(maxVisibleHunters, maxVisiblePreys, HUNTER_SPEED);

            launchAgent(hunter);
        }

	}
	
	public static void main(String[] args) {
        executeThisAgent(
                "--agentLogLevel", "INFO",
                "--start"
        );
    }

}
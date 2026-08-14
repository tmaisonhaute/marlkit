package marlkit.preyhunter.launchers;

import experience.TransitionExperienceBuilder;
import marlkit.preyhunter.agent.HunterAgentDDPG;
import marlkit.preyhunter.environment.EnvPreyVsHunter;

/**
 * Launcher for the PreyHunter experiment using DDPG for hunter agents.
 * 
 */
public class LauncherPVHDDPG extends LauncherPVH {

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
            HunterAgentDDPG hunter = new HunterAgentDDPG(maxVisibleHunters, maxVisiblePreys, HUNTER_SPEED);

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

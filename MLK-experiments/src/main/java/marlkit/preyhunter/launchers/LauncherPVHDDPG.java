package marlkit.preyhunter.launchers;

import experience.TransitionExperienceBuilder;
import marlkit.preyhunter.agent.HunterAgentDDPG;
import marlkit.preyhunter.environment.EnvPreyVsHunter;
import simulation.LauncherMetadata;

/**
 * Launcher for the PreyHunter experiment using DDPG for hunter agents.
 * 
 */
@LauncherMetadata(
		title = "DDPG Decentralized Training",
		documentationAnchor = "ddpg-decentralized-training")
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
//        ReadOnlyCriterion readOnlyEvaluationCriterion = ((MLKScheduler) getScheduler()).getReadOnlyEvaluationCriterion();

        for (int i = 0; i < NB_HUNTER_AGENTS; i++) {
            HunterAgentDDPG hunter = new HunterAgentDDPG(maxVisibleHunters, maxVisiblePreys, HUNTER_SPEED, null);

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

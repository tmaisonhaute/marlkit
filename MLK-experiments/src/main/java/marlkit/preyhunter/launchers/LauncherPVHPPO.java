package marlkit.preyhunter.launchers;

import marlkit.preyhunter.agent.HunterAgentPPO;

/**
 * Launcher for the PreyHunter experiment using PPO for hunter agents.
 */
public class LauncherPVHPPO extends LauncherPVH {

	
	@Override
	protected void launchHunters() {
		int maxVisibleHunters = HUNTERS_OBSERVE_OTHER_HUNTERS ? NB_HUNTER_AGENTS - 1 : 0;

        int maxVisiblePreys = NB_PREY_AGENTS;

        for (int i = 0; i < NB_HUNTER_AGENTS; i++) {
            HunterAgentPPO hunter = new HunterAgentPPO(maxVisibleHunters, maxVisiblePreys, NUMBER_OF_DIRECTIONS, HUNTER_SPEED);

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

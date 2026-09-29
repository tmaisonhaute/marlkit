package marlkit.preyhunter.launchers;

import communicationimplementation.BroadcastRelativeObservationPositions;
import marlkit.preyhunter.agent.HunterAgentCommunicatingDDPG;
import marlkit.preyhunter.agent.HunterAgentDDPG;
import simulation.MLKScheduler;
import util.criteria.ReadOnlyCriterion;
import simulation.LauncherMetadata;

/**
 * Launcher for the PreyHunter experiment using DDPG for hunter agents, with broadcast relative observation positions.
 * 
 * 
 */
@LauncherMetadata(
    title = "DDPG Broadcast Observation",
    documentationAnchor = "ddpg-broadcast-observation")
public class LauncherPVHDDPGBroadcastObservation extends LauncherPVHDDPG {
	
	@Override
	protected void launchHunters() {
		int maxVisibleHunters = HUNTERS_OBSERVE_OTHER_HUNTERS ? NB_HUNTER_AGENTS - 1 : 0;
        int maxVisiblePreys = NB_PREY_AGENTS;
        ReadOnlyCriterion readOnlyEvaluationCriterion = ((MLKScheduler) getScheduler()).getReadOnlyEvaluationCriterion();

        for (int i = 0; i < NB_HUNTER_AGENTS; i++) {
            HunterAgentDDPG hunter = new HunterAgentCommunicatingDDPG(maxVisibleHunters, maxVisiblePreys, HUNTER_SPEED, readOnlyEvaluationCriterion, new BroadcastRelativeObservationPositions());

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

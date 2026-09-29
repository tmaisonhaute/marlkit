package marlkit.preyhunter.launchers;

import java.util.ArrayList;
import java.util.List;

import communicationimplementation.BroadcastRelativeObservationPositions;
import marlkit.preyhunter.agent.HunterAgentCommunicatingMADDPG;
import marlkit.preyhunter.agent.HunterAgentMADDPG;
import simulation.MLKScheduler;
import util.criteria.ReadOnlyCriterion;
import simulation.LauncherMetadata;

/**
 * Launcher for the PreyHunter experiment using MADDPG hunter agents with
 * broadcast communication of relative observations.
 */
@LauncherMetadata(
    title = "MADDPG Broadcast Observation",
    documentationAnchor = "maddpg-broadcast-observation")
public class LauncherPVHMADDPGBroadcastObservation extends LauncherPVHMADDPG {

    /**
     * Creates the communicating MADDPG hunters, configures their access to the
     * target actors of all hunters, and launches them.
     */
    @Override
    protected void launchHunters() {
        int maxVisibleHunters = HUNTERS_OBSERVE_OTHER_HUNTERS ? NB_HUNTER_AGENTS - 1 : 0;
        int maxVisiblePreys = NB_PREY_AGENTS;

        ReadOnlyCriterion readOnlyEvaluationCriterion = ((MLKScheduler) getScheduler()).getReadOnlyEvaluationCriterion();
        List<HunterAgentMADDPG> hunters = new ArrayList<>();

        for (int i = 0; i < NB_HUNTER_AGENTS; i++) {
            HunterAgentMADDPG hunter = new HunterAgentCommunicatingMADDPG(maxVisibleHunters, maxVisiblePreys, HUNTER_SPEED, NB_HUNTER_AGENTS, readOnlyEvaluationCriterion, new BroadcastRelativeObservationPositions());
            hunters.add(hunter);
        }

        for (HunterAgentMADDPG hunter : hunters) {
            hunter.setOtherAgents(hunters);
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
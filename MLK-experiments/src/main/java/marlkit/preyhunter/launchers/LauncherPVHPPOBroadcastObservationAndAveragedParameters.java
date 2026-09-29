package marlkit.preyhunter.launchers;

import marlkit.preyhunter.agent.HunterAgentCommunicatingPPO;
import marlkit.preyhunter.communication.BroadcastObservationAndAveragedParameters;
import simulation.LauncherMetadata;

@LauncherMetadata(
    title = "PPO Broadcast Observation and Averaged Parameters",
    documentationAnchor = "ppo-broadcast-observation-and-averaged-parameters")
public class LauncherPVHPPOBroadcastObservationAndAveragedParameters extends LauncherPVH {

    protected static final double RECEIVED_PARAMETERS_WEIGHT = 0.5;

    @Override
    protected void launchHunters() {
        int maxVisibleHunters = HUNTERS_OBSERVE_OTHER_HUNTERS ? NB_HUNTER_AGENTS - 1 : 0;
        int maxVisiblePreys = NB_PREY_AGENTS;

        for (int i = 0; i < NB_HUNTER_AGENTS; i++) {
            HunterAgentCommunicatingPPO hunter = new HunterAgentCommunicatingPPO(maxVisibleHunters, maxVisiblePreys, 
            		NUMBER_OF_DIRECTIONS, HUNTER_SPEED, new BroadcastObservationAndAveragedParameters(RECEIVED_PARAMETERS_WEIGHT));
            launchAgent(hunter);
        }
    }

    public static void main(String[] args) {
        executeThisAgent("--agentLogLevel", "INFO", "--start");
    }
}
package marlkit.preyhunter.launchers;

import communicationimplementation.BroadcastAveragedPolicyParameters;
import marlkit.preyhunter.agent.HunterAgentCommunicatingPPO;
import simulation.LauncherMetadata;

@LauncherMetadata(
    title = "PPO Averaged Policy Parameters",
    documentationAnchor = "ppo-averaged-policy-parameters")
public class LauncherPVHPPOAveragedPolicyParameters extends LauncherPVH {

    protected static final double RECEIVED_PARAMETERS_WEIGHT = 0.5;

    @Override
    protected void launchHunters() {
        int maxVisibleHunters = HUNTERS_OBSERVE_OTHER_HUNTERS ? NB_HUNTER_AGENTS - 1 : 0;
        int maxVisiblePreys = NB_PREY_AGENTS;

        for (int i = 0; i < NB_HUNTER_AGENTS; i++) {
            HunterAgentCommunicatingPPO hunter = new HunterAgentCommunicatingPPO(maxVisibleHunters, maxVisiblePreys, NUMBER_OF_DIRECTIONS, HUNTER_SPEED, new BroadcastAveragedPolicyParameters(RECEIVED_PARAMETERS_WEIGHT));
            launchAgent(hunter);
        }
    }

    public static void main(String[] args) {
        executeThisAgent("--agentLogLevel", "INFO", "--start");
    }
}
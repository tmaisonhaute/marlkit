package marlkit.preyhunter.launchers;

import communicationimplementation.BroadcastAveragedPolicyParameters;
import marlkit.preyhunter.agent.HunterAgentCommunicating;

public class LauncherPVHAveragedPolicyParameters extends LauncherPVH {

    protected static final double RECEIVED_PARAMETERS_WEIGHT = 0.5;

    @Override
    protected void launchHunters() {
        int maxVisibleHunters = HUNTERS_OBSERVE_OTHER_HUNTERS ? NB_HUNTER_AGENTS - 1 : 0;
        int maxVisiblePreys = NB_PREY_AGENTS;

        for (int i = 0; i < NB_HUNTER_AGENTS; i++) {
            HunterAgentCommunicating hunter = new HunterAgentCommunicating(maxVisibleHunters, maxVisiblePreys, NUMBER_OF_DIRECTIONS, HUNTER_SPEED, new BroadcastAveragedPolicyParameters(RECEIVED_PARAMETERS_WEIGHT));
            launchAgent(hunter);
        }
    }

    public static void main(String[] args) {
        executeThisAgent("--agentLogLevel", "INFO", "--start");
    }
}
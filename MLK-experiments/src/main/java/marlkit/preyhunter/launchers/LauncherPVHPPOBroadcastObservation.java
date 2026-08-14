package marlkit.preyhunter.launchers;


import communicationimplementation.BroadcastRelativeObservationPositions;
import marlkit.preyhunter.agent.HunterAgentCommunicatingPPO;

public class LauncherPVHPPOBroadcastObservation extends LauncherPVH {
	
	@Override
	protected void launchHunters() {
        int maxVisibleHunters = HUNTERS_OBSERVE_OTHER_HUNTERS ? NB_HUNTER_AGENTS - 1 : 0;

        int maxVisiblePreys = NB_PREY_AGENTS;

        for (int i = 0; i < NB_HUNTER_AGENTS; i++) {
            HunterAgentCommunicatingPPO hunter = new HunterAgentCommunicatingPPO(maxVisibleHunters, maxVisiblePreys, NUMBER_OF_DIRECTIONS, HUNTER_SPEED
            		, new BroadcastRelativeObservationPositions());

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

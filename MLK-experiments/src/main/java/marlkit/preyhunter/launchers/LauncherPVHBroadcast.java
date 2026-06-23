package marlkit.preyhunter.launchers;


import communicationimplementation.BroadcastObservation;
import marlkit.preyhunter.agent.HunterAgentCommunicating;

public class LauncherPVHBroadcast extends LauncherPVH {
	
	@Override
	protected void launchHunters() {
        int maxVisibleHunters = HUNTERS_OBSERVE_OTHER_HUNTERS ? NB_HUNTER_AGENTS - 1 : 0;

        int maxVisiblePreys = NB_PREY_AGENTS;

        for (int i = 0; i < NB_HUNTER_AGENTS; i++) {
            HunterAgentCommunicating hunter = new HunterAgentCommunicating(maxVisibleHunters, maxVisiblePreys, NUMBER_OF_DIRECTIONS, HUNTER_SPEED
            		, new BroadcastObservation());

            launchAgent(hunter);
        }
    }

}

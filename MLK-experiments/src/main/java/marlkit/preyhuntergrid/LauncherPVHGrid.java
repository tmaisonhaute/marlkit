package marlkit.preyhuntergrid;

import agent.AgentStandard;
import madkit.simulation.EngineAgents;
import marlkit.preyhunter.scheduler.SchedulerPVH;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(
        scheduler = SchedulerPVH.class,
        environment = EnvPreyVsHunterGrid.class,
        model = MLKModel.class,
        viewers = {ViewerPVHGrid.class}
)
public class LauncherPVHGrid extends MLKLauncher {

    private static final int NB_HUNTER_AGENTS = 2;
    private static final int NB_PREY_AGENTS = 1;

    @Override
    protected void onLaunchSimulatedAgents() {
        for (int i = 0; i < NB_HUNTER_AGENTS; i++) {
            AgentStandard agent = new HunterGridAgent();
            launchAgent(agent);
        }

        for (int i = 0; i < NB_PREY_AGENTS; i++) {
            AgentStandard agent = new PreyGridAgent();
            launchAgent(agent);
        }
    }

    public static void main(String[] args) {
        executeThisAgent(
                "--agentLogLevel", "INFO",
                "--start"
        );
    }
}
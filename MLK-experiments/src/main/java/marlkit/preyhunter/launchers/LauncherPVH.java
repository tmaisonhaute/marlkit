package marlkit.preyhunter.launchers;

import agent.AgentStandard;
import madkit.simulation.EngineAgents;
import marlkit.preyhunter.agent.HunterAgent;
import marlkit.preyhunter.agent.PreyAgent;
import marlkit.preyhunter.environment.EnvPreyVsHunter;
import marlkit.preyhunter.scheduler.SchedulerPVH;
import marlkit.preyhunter.viewer.ViewerPVH;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(
        scheduler = SchedulerPVH.class,
        environment = EnvPreyVsHunter.class,
        model = MLKModel.class,
        viewers = { ViewerPVH.class }
)
public class LauncherPVH extends MLKLauncher {

    private static final int NB_HUNTER_AGENTS = 2;
    private static final int NB_PREY_AGENTS = 1;

    @Override
    protected void onLaunchSimulatedAgents() {
        for (int i = 0; i < NB_HUNTER_AGENTS; i++) {
            AgentStandard agent = new HunterAgent();
            launchAgent(agent);
        }

        for (int i = 0; i < NB_PREY_AGENTS; i++) {
            AgentStandard agent = new PreyAgent();
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

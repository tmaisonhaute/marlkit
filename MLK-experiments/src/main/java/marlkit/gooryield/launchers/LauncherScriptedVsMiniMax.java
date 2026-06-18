package marlkit.gooryield.launchers;

import agent.AgentStandard;
import madkit.simulation.EngineAgents;
import marlkit.gooryield.SchedulerGoOrYield;
import marlkit.gooryield.ViewerGoOrYield;
import marlkit.gooryield.agent.AgentGoOrYieldMinMax;
import marlkit.gooryield.agent.AgentScripted;
import marlkit.gooryield.environment.EnvGoOrYield;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(
        scheduler = SchedulerGoOrYield.class,
        environment = EnvGoOrYield.class,
        model = MLKModel.class,
        viewers = { ViewerGoOrYield.class })
public class LauncherScriptedVsMiniMax extends MLKLauncher {

    protected int getSwitchPeriod() {
        return 5000;
    }

    @Override
    protected void onLaunchSimulatedAgents() {
        AgentStandard testedAgent = new AgentGoOrYieldMinMax();
        AgentStandard scriptedAgent = new AgentScripted(getSwitchPeriod());

        launchAgent(testedAgent);
        launchAgent(scriptedAgent);
    }

    public static void main(String[] args) {
        executeThisAgent("--agentLogLevel", "INFO", "--start");
    }
}
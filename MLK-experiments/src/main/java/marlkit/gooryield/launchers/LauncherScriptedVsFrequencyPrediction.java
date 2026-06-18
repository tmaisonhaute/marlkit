package marlkit.gooryield.launchers;

import java.util.List;

import madkit.simulation.EngineAgents;
import marlkit.gooryield.SchedulerGoOrYield;
import marlkit.gooryield.ViewerGoOrYield;
import marlkit.gooryield.agent.AgentGoOrYieldActionFrequenciesPrediction;
import marlkit.gooryield.agent.AgentScripted;
import marlkit.gooryield.environment.EnvGoOrYield;
import simulation.MLKLauncher;
import simulation.MLKModel;

@EngineAgents(
        scheduler = SchedulerGoOrYield.class,
        environment = EnvGoOrYield.class,
        model = MLKModel.class,
        viewers = { ViewerGoOrYield.class })
public class LauncherScriptedVsFrequencyPrediction extends MLKLauncher {

    protected int getSwitchPeriod() {
        return 5000;
    }

    protected int getWindowSize() {
        return 1000;
    }

    @Override
    protected void onLaunchSimulatedAgents() {
        AgentGoOrYieldActionFrequenciesPrediction testedAgent =
                new AgentGoOrYieldActionFrequenciesPrediction(getWindowSize());
        AgentScripted scriptedAgent =
                new AgentScripted(getSwitchPeriod());

        testedAgent.setOtherAgents(List.of(scriptedAgent));

        launchAgent(testedAgent);
        launchAgent(scriptedAgent);
    }

    public static void main(String[] args) {
        executeThisAgent("--agentLogLevel", "INFO", "--start");
    }
}
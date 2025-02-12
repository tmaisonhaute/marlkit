package simulation;

import environment.EnvironmentStandard;
import madkit.simulation.EngineAgents;
import madkit.simulation.SimuLauncher;

@EngineAgents(
        scheduler=MLKSchedulerStandard.class,
        environment = EnvironmentStandard.class,
        model = MLKModel.class,
        viewers = {MLKViewer.class}
)
public abstract class MLKLauncher extends SimuLauncher {
	@Override
	protected abstract void onLaunchSimulatedAgents(); 
        
    public static void main(String[] args) {
		executeThisAgent(
				"--agentLogLevel", "ALL"
				, "--start"
//				,"--viewers",MyViewer.class.getName()
        );
    }
}

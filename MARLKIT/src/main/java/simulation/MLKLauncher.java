package simulation;

import environment.EnvironmentStandard;
import madkit.simulation.EngineAgents;
import madkit.simulation.SimuLauncher;

@EngineAgents(
        environment = EnvironmentStandard.class,
        model = MLKModel.class
//        ,       viewers = {MLKViewer.class}
)
public abstract class MLKLauncher extends SimuLauncher {
	@Override
	protected abstract void onLaunchSimulatedAgents(); 
        
}

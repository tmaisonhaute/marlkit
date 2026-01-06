package simulation;

import environment.EnvironmentStandard;
import madkit.simulation.EngineAgents;
import madkit.simulation.SimuLauncher;

/**
 * Abstract launcher for MARLKIT simulations.
 * Configures the simulation engine with environment and model components.
 */
@EngineAgents(
        environment = EnvironmentStandard.class,
        model = MLKModel.class
//        ,       viewers = {MLKViewer.class}
)
public abstract class MLKLauncher extends SimuLauncher {
	/**
	 * Called to launch and initialize simulated agents.
	 * Subclasses must implement this to create their specific agent setup.
	 */
	@Override
	protected abstract void onLaunchSimulatedAgents(); 
        
}

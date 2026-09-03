package experiment;

import java.util.List;

import experiment.configuration.ExperimentConfiguration;
import madkit.kernel.Agent;

/**
 * A runner that launches a list of experiment configurations.
 * 
 * <p>
 * This class is responsible for iterating through a list of experiment configurations and 
 * launching each one using the {@link ConfigurableExperimentLauncher}.
 * </p>
 */
public class ConfigurationRunner extends Agent{

    private final List<ExperimentConfiguration> configurations;

    /**
     * Creates a configuration runner with a list of experiment configurations.
     * @param configurations the list of experiment configurations to run
     */
    public ConfigurationRunner(List<ExperimentConfiguration> configurations) {
        this.configurations = List.copyOf(configurations);
    }
    
    @Override
    protected void onActivation() {
    	super.onActivation();
    	launchAll();
    	
    }

    /**
     * Launches all experiment configurations in the list.
     * @param args the command line arguments to pass to each experiment
     */
    protected void launchAll(String... args) {
    	
		for (ExperimentConfiguration configuration : configurations) {
			ConfigurableExperimentLauncher configLaunch = new ConfigurableExperimentLauncher();
			configLaunch.setConfiguration(configuration);
			launchAgent(configLaunch);
			
		}
    	
    }

}
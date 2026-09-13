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
    protected final int numberOfRuns;

    /**
     * Creates a configuration runner with a list of experiment configurations.
     * @param configurations the list of experiment configurations to run
     */
    public ConfigurationRunner(List<ExperimentConfiguration> configurations) {
        this(configurations, 1);
    }
    
    /**
     * Creates a configuration runner with a list of experiment configurations and a specified number of runs.
     * @param configurations the list of experiment configurations to run
     * @param numberOfRuns the number of times to run each configuration
     */
    public ConfigurationRunner(List<ExperimentConfiguration> configurations, int numberOfRuns) {
        this.configurations = List.copyOf(configurations);
        this.numberOfRuns = numberOfRuns;
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
			for (int i = 0; i < numberOfRuns; i++) {
				ConfigurableExperimentLauncher configLaunch = new ConfigurableExperimentLauncher();
				configLaunch.setConfiguration(configuration);
				launchAgent(configLaunch);
			}
			
		}
    	
    }

}
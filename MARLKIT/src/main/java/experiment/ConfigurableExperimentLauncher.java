package experiment;

import java.util.List;
import java.util.OptionalInt;

import agent.MLKAgent;
import environment.MLKEnvironment;
import experiment.configuration.ExperimentConfiguration;
import madkit.kernel.Scheduler;
import madkit.simulation.EngineAgents;
import madkit.simulation.SimuAgent;
import madkit.simulation.SimuEnvironment;
import simulation.MLKLauncher;
import simulation.MLKModel;
import simulation.MLKScheduler;

@EngineAgents(
        model = MLKModel.class
)
public class ConfigurableExperimentLauncher extends MLKLauncher {
	
	protected ExperimentConfiguration configuration;
	protected int seedLaunchIndex = 0;
	
	/**
	 * Creates a new instance of ConfigurableExperimentLauncher with the specified seed launch index.
	 * 
	 * <p> 
	 * The seed launch index is used to determine the starting point. It is add to the seedIndex of the configuration if it is not null. 
	 * </p>
	 * @param seedLaunchIndex
	 */
	public ConfigurableExperimentLauncher(int seedLaunchIndex) {
		super();
		this.seedLaunchIndex = seedLaunchIndex;
	}

	
	public void setConfiguration(ExperimentConfiguration config) {
		this.configuration = config;
	}
	
	public ExperimentConfiguration getConfiguration() {
		return this.configuration;
	}

    public static void main(String[] args) {
        executeThisAgent(args);
    }
    
    
    @Override
    public void onInitializeSimulationSeedIndex() {
    	super.onInitializeSimulationSeedIndex();
    	int currentSeed = seedLaunchIndex;
    	OptionalInt forcedSeedIndex = configuration.getSeedIndex();
    	if (forcedSeedIndex != null && forcedSeedIndex.isPresent()) {
    		currentSeed = seedLaunchIndex + forcedSeedIndex.getAsInt();
    	}
    	setPRNGSeedIndex(currentSeed);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <E extends SimuEnvironment> E onLaunchEnvironment() {
        ensureConfigurationIsSet();

        MLKEnvironment environment = configuration.createEnvironment();
        environment.setSystemEvaluator(configuration.createSystemEvaluator());

        if (!(environment instanceof SimuEnvironment simuEnvironment)) {
            throw new IllegalStateException(
                    "Configured environment must extend SimuEnvironment: "
                            + environment.getClass().getName()
            );
        }

        launchAgent(simuEnvironment, Integer.MAX_VALUE);
        return (E) simuEnvironment;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <S extends Scheduler<?>> S onLaunchScheduler() {
        ensureConfigurationIsSet();

        MLKScheduler scheduler = configuration.createScheduler();
        launchAgent(scheduler, Integer.MAX_VALUE);

        return (S) scheduler;
    }

    @Override
    protected void onLaunchSimulatedAgents() {
        ensureConfigurationIsSet();

        List<MLKAgent> agents = configuration.createAgents();

        for (MLKAgent agent : agents) {
            if (!(agent instanceof SimuAgent simuAgent)) {
                throw new IllegalStateException("Configured agent must extend SimuAgent: " + agent.getClass().getName());
            }

            launchAgent(simuAgent);
        }
    }

    /**
     * Ensures that the experiment configuration has been set before launching the experiment.
     */
    private void ensureConfigurationIsSet() {
        if (configuration == null) {
            throw new IllegalStateException(
                    "No ExperimentConfiguration has been set. "
                            + "Use ConfigurableExperimentLauncher.launch(config, args)."
            );
        }
    }
    
    @Override
    protected void onLive() {
    	while(getScheduler().isAlive()) {
    		pause(1000);
    		
    	}
    }
    
    @Override
    protected void onEnd() {
        super.onEnd();

        getLogger().talk("Experiment completed: " + configuration.getName());
    }
    
    @Override
    public String getName() {
    	return configuration.getName();
    }
}


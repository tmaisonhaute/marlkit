package experiment;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

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

    private static ExperimentConfiguration configuration;
    private static CountDownLatch completionLatch;

    public static void launch(ExperimentConfiguration config, String... args) {
        configuration = Objects.requireNonNull(config, "config");
        main(args);
    }
    
    public static synchronized void launchAndWait(ExperimentConfiguration config, String... args) {
        configuration = Objects.requireNonNull(config, "config");
        completionLatch = new CountDownLatch(1);

        main(args);

        try {
            completionLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Experiment launch was interrupted.", e);
        } finally {
            configuration = null;
            completionLatch = null;
        }
    }

    public static void main(String[] args) {
        executeThisAgent(args);
    }

    public static ExperimentConfiguration getConfiguration() {
        return configuration;
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
                throw new IllegalStateException(
                        "Configured agent must extend SimuAgent: "
                                + agent.getClass().getName()
                );
            }

            launchAgent(simuAgent);
        }
    }

    private static void ensureConfigurationIsSet() {
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
        if (completionLatch != null) {
            completionLatch.countDown();
        }
    }
}
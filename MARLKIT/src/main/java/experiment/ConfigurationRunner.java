package experiment;

import java.util.List;

import experiment.configuration.ExperimentConfiguration;

public class ConfigurationRunner {

    private final List<ExperimentConfiguration> configurations;

    public ConfigurationRunner(List<ExperimentConfiguration> configurations) {
        this.configurations = List.copyOf(configurations);
    }

    public void launchAll(String... args) {
        for (ExperimentConfiguration configuration : configurations) {
//            ConfigurableExperimentLauncher.launch(configuration, args);
            ConfigurableExperimentLauncher.launchAndWait(configuration, args);
        }
    }

    public static void launch(List<ExperimentConfiguration> configurations, String... args) {
        new ConfigurationRunner(configurations).launchAll(args);
    }
}
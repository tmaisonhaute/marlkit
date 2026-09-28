package experiment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.testng.annotations.Test;

import experiment.configuration.AgentGroupConfiguration;
import experiment.configuration.EnvironmentModule;
import experiment.configuration.ExperimentConfiguration;
import experiment.configuration.RewardModelModule;
import experiment.configuration.SchedulerModule;
import experiment.configuration.SystemEvaluatorModule;

/**
 * Contract tests for seed allocation across repeated experiment runs.
 *
 * <p>The run index is provided by {@link ConfigurationRunner} when it creates
 * each {@link ConfigurableExperimentLauncher}. A configured seed index is the
 * starting point of that configuration's seed series. Without a configured
 * seed index, the run index itself is used.</p>
 */
public class ConfigurableExperimentSeedIntegrationTest {

    @Test
    public void givenConfigurationStartingAtFortyTwo_whenThreeRunsAreInitialized_thenSeedIndexesAreFortyTwoFortyThreeAndFortyFour() {
        // Given
        ExperimentConfiguration configuration = validConfiguration("configured-seed").seedIndex(42).build();
        RecordingLauncher firstRun = launcherFor(configuration, 0);
        RecordingLauncher secondRun = launcherFor(configuration, 1);
        RecordingLauncher thirdRun = launcherFor(configuration, 2);

        // When
        firstRun.onInitializeSimulationSeedIndex();
        secondRun.onInitializeSimulationSeedIndex();
        thirdRun.onInitializeSimulationSeedIndex();

        // Then
        assertThat(capturedSeedIndex(firstRun)).isEqualTo(42);
        assertThat(capturedSeedIndex(secondRun)).isEqualTo(43);
        assertThat(capturedSeedIndex(thirdRun)).isEqualTo(44);
    }

    @Test
    public void givenConfigurationWithoutSeedIndex_whenThreeRunsAreInitialized_thenSeedIndexesAreRunIndexes() {
        // Given
        ExperimentConfiguration configuration = validConfiguration("default-seed").build();
        RecordingLauncher firstRun = launcherFor(configuration, 0);
        RecordingLauncher secondRun = launcherFor(configuration, 1);
        RecordingLauncher thirdRun = launcherFor(configuration, 2);

        // When
        firstRun.onInitializeSimulationSeedIndex();
        secondRun.onInitializeSimulationSeedIndex();
        thirdRun.onInitializeSimulationSeedIndex();

        // Then
        assertThat(capturedSeedIndex(firstRun)).isEqualTo(0);
        assertThat(capturedSeedIndex(secondRun)).isEqualTo(1);
        assertThat(capturedSeedIndex(thirdRun)).isEqualTo(2);
    }

    @Test
    public void givenTwoConfigurations_whenTheirRunsAreInitialized_thenEachConfigurationUsesAnIndependentSeedSeries() {
        // Given
        ExperimentConfiguration configurationA = validConfiguration("configuration-a").seedIndex(42).build();
        ExperimentConfiguration configurationB = validConfiguration("configuration-b").build();

        List<RecordingLauncher> configurationARuns = launchersFor(configurationA, 3);
        List<RecordingLauncher> configurationBRuns = launchersFor(configurationB, 3);

        // When
        configurationARuns.forEach(ConfigurableExperimentLauncher::onInitializeSimulationSeedIndex);
        configurationBRuns.forEach(ConfigurableExperimentLauncher::onInitializeSimulationSeedIndex);

        // Then
        assertThat(configurationARuns)
                .extracting(ConfigurableExperimentSeedIntegrationTest::capturedSeedIndex)
                .containsExactly(42, 43, 44);

        assertThat(configurationBRuns)
                .extracting(ConfigurableExperimentSeedIntegrationTest::capturedSeedIndex)
                .containsExactly(0, 1, 2);
    }

    @Test
    public void givenNonZeroRunIndexAndConfiguredSeed_whenInitialized_thenRunIndexIsAddedToConfiguredSeed() {
        // Given
        ExperimentConfiguration configuration = validConfiguration("offset-seed").seedIndex(100).build();
        RecordingLauncher launcher = launcherFor(configuration, 7);

        // When
        launcher.onInitializeSimulationSeedIndex();

        // Then
        assertThat(capturedSeedIndex(launcher)).isEqualTo(107);
    }

    @Test
    public void givenNonZeroRunIndexAndNoConfiguredSeed_whenInitialized_thenRunIndexIsUsedDirectly() {
        // Given
        ExperimentConfiguration configuration = validConfiguration("run-index-seed").build();
        RecordingLauncher launcher = launcherFor(configuration, 7);

        // When
        launcher.onInitializeSimulationSeedIndex();

        // Then
        assertThat(capturedSeedIndex(launcher)).isEqualTo(7);
    }

    private static List<RecordingLauncher> launchersFor(ExperimentConfiguration configuration, int numberOfRuns) {
        return java.util.stream.IntStream.range(0, numberOfRuns)
                .mapToObj(runIndex -> launcherFor(configuration, runIndex))
                .toList();
    }

    private static RecordingLauncher launcherFor(ExperimentConfiguration configuration, int seedLaunchIndex) {
        RecordingLauncher launcher = new RecordingLauncher(seedLaunchIndex);
        launcher.setConfiguration(configuration);
        return launcher;
    }

    private static int capturedSeedIndex(RecordingLauncher launcher) {
        return launcher.getLastAssignedSeedIndex();
    }

    private static class RecordingLauncher extends ConfigurableExperimentLauncher {

        private Integer lastAssignedSeedIndex;

        private RecordingLauncher(int seedLaunchIndex) {
            super(seedLaunchIndex);
        }

        @Override
        public void setPRNGSeedIndex(int seedIndex) {
            lastAssignedSeedIndex = seedIndex;
        }

        private int getLastAssignedSeedIndex() {
            assertThat(lastAssignedSeedIndex)
                    .as("a seed index must have been assigned")
                    .isNotNull();

            return lastAssignedSeedIndex;
        }
    }

    private static ExperimentConfiguration.Builder validConfiguration(String name) {
        return ExperimentConfiguration.named(name)
                .environment(mock(EnvironmentModule.class))
                .rewardModel(mock(RewardModelModule.class))
                .scheduler(mock(SchedulerModule.class))
                .agentGroup(mock(AgentGroupConfiguration.class))
                .systemEvaluator(mock(SystemEvaluatorModule.class));
    }
}
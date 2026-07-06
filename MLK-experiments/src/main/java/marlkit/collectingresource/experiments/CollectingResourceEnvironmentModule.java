package marlkit.collectingresource.experiments;

import java.util.Objects;

import environment.MLKEnvironment;
import experiment.configuration.EnvironmentModule;
import marlkit.collectingresource.environment.EnvCollectingResource;
import marlkit.collectingresource.scenario.ScenarioCollectingResource;
import reward.RewardModel;

public class CollectingResourceEnvironmentModule extends EnvironmentModule {

    private final int width;
    private final int height;
    private final ScenarioCollectingResource scenario;

    public CollectingResourceEnvironmentModule(int width, int height, ScenarioCollectingResource scenario) {
        super(EnvCollectingResource.class);
        this.width = width;
        this.height = height;
        this.scenario = Objects.requireNonNull(scenario, "scenario");
    }

    @Override
    public MLKEnvironment createEnvironment(RewardModel rewardModel) {
        return new EnvCollectingResource(width, height, rewardModel, scenario);
    }
}
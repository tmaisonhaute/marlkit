package experiment.configuration;

import java.lang.reflect.Constructor;
import java.util.Objects;

import environment.MLKEnvironment;
import reward.RewardModel;


/**
 * Module responsible for creating the environment of an experiment.
 * <p>
 * This module stores the concrete environment class selected for a configuration
 * and creates a fresh environment instance when the experiment is built.
 * </p>
 * <p>
 * The environment class must expose a constructor accepting a {@link RewardModel}.
 * More complex environments requiring additional constructor parameters should be
 * handled by a specialized module overriding {@link #createEnvironment(RewardModel)}.
 * </p>
 */

public class EnvironmentModule {

    private final Class<? extends MLKEnvironment> environmentClass;

    /**
     * Creates an environment module.
     *
     * @param environmentClass the environment class to instantiate
     */
    public EnvironmentModule(Class<? extends MLKEnvironment> environmentClass) {
        this.environmentClass = Objects.requireNonNull(environmentClass, "environmentClass");
    }

    /**
     * Creates a fresh environment instance.
     *
     * @param rewardModel the reward model to pass to the environment constructor
     * @return a new environment
     */
    public MLKEnvironment createEnvironment(RewardModel rewardModel) {
        try {
            Constructor<? extends MLKEnvironment> constructor =
                    environmentClass.getConstructor(RewardModel.class);

            return constructor.newInstance(rewardModel);

        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Cannot instantiate environment: " + environmentClass.getName()
                            + ". Expected constructor Environment(RewardModel).",
                    e
            );
        }
    }

    /**
     * Returns the environment class used by this module.
     *
     * @return the environment class
     */
    public Class<? extends MLKEnvironment> getEnvironmentClass() {
        return environmentClass;
    }
}
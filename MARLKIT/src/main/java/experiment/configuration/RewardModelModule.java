package experiment.configuration;

import reward.RewardModel;

public class RewardModelModule {

    private final Class<? extends RewardModel> rewardModelClass;

    public RewardModelModule(Class<? extends RewardModel> rewardModelClass) {
        this.rewardModelClass = rewardModelClass;
    }

    public RewardModel createRewardModel() {
        try {
            return rewardModelClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Cannot instantiate reward model: " + rewardModelClass.getName(),
                    e
            );
        }
    }

    public Class<? extends RewardModel> getRewardModelClass() {
        return rewardModelClass;
    }
}
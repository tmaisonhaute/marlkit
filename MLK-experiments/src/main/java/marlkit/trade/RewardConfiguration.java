package marlkit.trade;

import java.util.Map;

import agent.MLKAgent;
import environment.reward.Reward;

public abstract class RewardConfiguration {
    public abstract Map<MLKAgent, Reward> computeRewards(Map<MLKAgent, ResourceQuantify> receivedResource, Map<ResourceType,Float> basePrices);
}

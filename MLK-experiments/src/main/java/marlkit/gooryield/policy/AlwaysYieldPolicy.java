package marlkit.gooryield.policy;
import java.util.random.RandomGenerator;

import agent.action.Action;
import learning.policies.StochasticFixedPolicy;
import marlkit.gooryield.agent.action.ActionYield;
import util.MapProba;

public class AlwaysYieldPolicy extends StochasticFixedPolicy {

    public AlwaysYieldPolicy(RandomGenerator prng) {
        super(createProbabilities(prng));
    }

    private static MapProba<Action> createProbabilities(RandomGenerator prng) {
        MapProba<Action> probabilities = new MapProba<>(prng);
        probabilities.put(new ActionYield(), 1.0);
        return probabilities;
    }
}
package marlkit.gooryield.policy;

import java.util.random.RandomGenerator;

import agent.action.Action;
import learning.policies.StochasticFixedPolicy;
import marlkit.gooryield.agent.action.ActionGo;
import marlkit.gooryield.agent.action.ActionYield;
import util.MapProba;

public class Go75Yield25Policy extends StochasticFixedPolicy {

    public Go75Yield25Policy(RandomGenerator prng) {
        super(createProbabilities(prng));
    }

    private static MapProba<Action> createProbabilities(RandomGenerator prng) {
        MapProba<Action> probabilities = new MapProba<>(prng);
        probabilities.put(new ActionGo(), 0.75);
        probabilities.put(new ActionYield(), 0.25);
        return probabilities;
    }
}

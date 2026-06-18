package marlkit.gooryield.policy;

import java.util.random.RandomGenerator;

import agent.action.Action;
import learning.policies.StochasticFixedPolicy;
import marlkit.gooryield.agent.action.ActionGo;
import marlkit.gooryield.agent.action.ActionYield;
import util.MapProba;

public class Go50Yield50Policy extends StochasticFixedPolicy {

    public Go50Yield50Policy(RandomGenerator prng) {
        super(createProbabilities(prng));
    }

    private static MapProba<Action> createProbabilities(RandomGenerator prng) {
        MapProba<Action> probabilities = new MapProba<>(prng);
        probabilities.put(new ActionGo(), 0.5);
        probabilities.put(new ActionYield(), 0.5);
        return probabilities;
    }
}

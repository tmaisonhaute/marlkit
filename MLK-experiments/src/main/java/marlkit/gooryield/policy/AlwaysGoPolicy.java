package marlkit.gooryield.policy;
import java.util.random.RandomGenerator;

import agent.action.Action;
import learning.policies.StochasticFixedPolicy;
import marlkit.gooryield.agent.action.ActionGo;
import util.MapProba;

public class AlwaysGoPolicy extends StochasticFixedPolicy {

    public AlwaysGoPolicy(RandomGenerator prng) {
        super(createProbabilities(prng));
    }

    private static MapProba<Action> createProbabilities(RandomGenerator prng) {
        MapProba<Action> probabilities = new MapProba<>(prng);
        probabilities.put(new ActionGo(), 1.0);
        return probabilities;
    }
}
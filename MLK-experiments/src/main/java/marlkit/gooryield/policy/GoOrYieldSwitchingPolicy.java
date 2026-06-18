package marlkit.gooryield.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

import agent.MLKAgent;
import agent.action.Action;
import learning.Policy;
import learning.policies.PolicyInput;
import learning.policies.StochasticFixedPolicy;

public class GoOrYieldSwitchingPolicy implements Policy {

    protected MLKAgent agent;
    protected final List<StochasticFixedPolicy> availablePolicies;
    protected StochasticFixedPolicy currentPolicy;

    public GoOrYieldSwitchingPolicy(RandomGenerator prng) {
        this.availablePolicies = new ArrayList<>();
        this.availablePolicies.add(new AlwaysYieldPolicy(prng));
        this.availablePolicies.add(new AlwaysGoPolicy(prng));
        this.availablePolicies.add(new Go50Yield50Policy(prng));
        this.availablePolicies.add(new Go75Yield25Policy(prng));
        this.availablePolicies.add(new Go25Yield75Policy(prng));
        this.currentPolicy = availablePolicies.get(0);
    }

    @Override
    public void init(MLKAgent agent) {
        this.agent = agent;
        for (StochasticFixedPolicy policy : availablePolicies) {
            policy.init(agent);
        }
        selectRandomPolicy();
    }

    @Override
    public MLKAgent getAgent() {
        return agent;
    }

    @Override
    public Action selectAction(PolicyInput input) {
        return currentPolicy.selectAction(input);
    }

    public void selectRandomPolicy() {
        int index = prng().nextInt(availablePolicies.size());
        currentPolicy = availablePolicies.get(index);
    }

    public StochasticFixedPolicy getCurrentPolicy() {
        return currentPolicy;
    }

    public List<StochasticFixedPolicy> getAvailablePolicies() {
        return new ArrayList<>(availablePolicies);
    }
}
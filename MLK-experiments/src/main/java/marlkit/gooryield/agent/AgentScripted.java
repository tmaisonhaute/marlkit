package marlkit.gooryield.agent;

import agent.AgentStandard;
import marlkit.gooryield.algorithm.RandomPolicySwitchAlgorithm;
import marlkit.gooryield.policy.GoOrYieldSwitchingPolicy;

public class AgentScripted extends AgentStandard {

    protected final int switchPeriod;

    public AgentScripted(int switchPeriod) {
        super();
        this.switchPeriod = switchPeriod;
    }

    @Override
    protected void onActivation() {
        GoOrYieldSwitchingPolicy policy = new GoOrYieldSwitchingPolicy(prng());
        RandomPolicySwitchAlgorithm algorithm =
                new RandomPolicySwitchAlgorithm(policy, switchPeriod);

        setPolicy(policy);
        setAlgorithm(algorithm);

        super.onActivation();
    }
}
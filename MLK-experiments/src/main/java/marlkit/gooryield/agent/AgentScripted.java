package marlkit.gooryield.agent;

import agent.AgentStandard;
import learning.Algorithm;
import marlkit.gooryield.algorithm.SequentialPolicySwitchAlgorithm;
import marlkit.gooryield.policy.GoOrYieldSwitchingPolicy;

public class AgentScripted extends AgentStandard {

    protected final int switchPeriod;

    public AgentScripted(int switchPeriod) {
        super();
        this.switchPeriod = switchPeriod;
        
        GoOrYieldSwitchingPolicy policy = new GoOrYieldSwitchingPolicy();
        Algorithm algorithm = new SequentialPolicySwitchAlgorithm(policy, switchPeriod);
        setPolicy(policy);
        setAlgorithm(algorithm);
    }

}
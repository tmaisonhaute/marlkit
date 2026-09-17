package marlkit.gooryield.experiments;

import experiment.configuration.LearningComponents;
import experiment.configuration.LearningComponentsCreator;
import experiment.configuration.agentspec.AgentSpec;
import learning.Algorithm;
import marlkit.gooryield.algorithm.SequentialPolicySwitchAlgorithm;
import marlkit.gooryield.policy.GoOrYieldSwitchingPolicy;

public class ScriptedLearningComponentsCreator extends LearningComponentsCreator {

    private final int switchPeriod;

    public ScriptedLearningComponentsCreator(int switchPeriod) {
        this.switchPeriod = switchPeriod;
    }

    @Override
    public LearningComponents createLearning(AgentSpec agentSpec) {
        GoOrYieldSwitchingPolicy policy = new GoOrYieldSwitchingPolicy();

        Algorithm algorithm = new SequentialPolicySwitchAlgorithm(policy, switchPeriod);

        return new LearningComponents(policy, algorithm);
    }
}
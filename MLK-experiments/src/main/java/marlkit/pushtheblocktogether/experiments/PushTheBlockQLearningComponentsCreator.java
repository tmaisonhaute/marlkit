package marlkit.pushtheblocktogether.experiments;

import experiment.configuration.LearningComponents;
import experiment.configuration.LearningComponentsCreator;
import experiment.configuration.agentspec.AgentSpec;
import learning.algorithms.QLearning;
import learning.explorationstrategies.EpsilonGreedyExponentialDecay;
import learning.policies.QValueBasedPolicy;

public class PushTheBlockQLearningComponentsCreator extends LearningComponentsCreator {

    private static final double DEFAULT_INITIAL_EPSILON = 1.0;
    private static final double DEFAULT_EPSILON_DECAY = 0.001;

    private static final double DEFAULT_ALPHA = 0.2;
    private static final double DEFAULT_GAMMA = 0.95;

    @Override
    public LearningComponents createLearning(AgentSpec agentSpec) {
        QValueBasedPolicy policy = new QValueBasedPolicy(
                agentSpec.getPossibleActions(),
                DEFAULT_INITIAL_EPSILON,
                new EpsilonGreedyExponentialDecay(DEFAULT_INITIAL_EPSILON, DEFAULT_EPSILON_DECAY)
        );

        QLearning algorithm = new QLearning(
                policy,
                agentSpec.getPossibleActions(),
                DEFAULT_ALPHA,
                DEFAULT_GAMMA
        );

        return new LearningComponents(policy, algorithm);
    }
}
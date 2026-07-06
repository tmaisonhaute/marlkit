package marlkit.collectingresource.experiments;

import java.util.List;

import agent.action.Action;
import experiment.configuration.LearningComponents;
import experiment.configuration.LearningComponentsCreator;
import experiment.configuration.agentspec.AgentSpec;
import learning.algorithms.QLearning;
import learning.explorationstrategies.EpsilonGreedyExponentialDecay;
import learning.policies.QValueBasedPolicy;

public class CollectingResourceQLearningComponentsCreator extends LearningComponentsCreator {

    private static final double INITIAL_EPSILON = 1.0;
    private static final double EPSILON_DECAY = 0.003;

    private static final double ALPHA = 0.2;
    private static final double GAMMA = 0.95;

    @Override
    public LearningComponents createLearning(AgentSpec agentSpec) {
        List<Action> possibleActions = agentSpec.getPossibleActions();

        QValueBasedPolicy policy = new QValueBasedPolicy(
                possibleActions,
                INITIAL_EPSILON,
                new EpsilonGreedyExponentialDecay(INITIAL_EPSILON, EPSILON_DECAY)
        );

        QLearning algorithm = new QLearning(
                policy,
                possibleActions,
                ALPHA,
                GAMMA
        );

        return new LearningComponents(policy, algorithm);
    }
}
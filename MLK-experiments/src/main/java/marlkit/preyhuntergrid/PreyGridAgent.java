package marlkit.preyhuntergrid;

import java.util.ArrayList;
import java.util.List;

import agent.action.Action;
import agent.action.Move2DInt;
import learning.algorithms.Sarsa;
import learning.explorationstrategies.EpsilonGreedyExponentialDecay;
import learning.policies.QValueBasedPolicy;
import marlkit.preyhunter.agent.PreyAgent;

/**
 * Prey agent configured for the grid PreyHunter environment.
 */
public class PreyGridAgent extends PreyAgent {

    private static final double INITIAL_Q_VALUE = 1.0;
    private static final double INITIAL_EPSILON = 1.0;
    private static final double EPSILON_DECAY = 0.001;
    private static final double LEARNING_RATE = 0.2;
    private static final double DISCOUNT_FACTOR = 0.95;

    public PreyGridAgent() {
        this(createPolicy());
    }

    private PreyGridAgent(QValueBasedPolicy policy) {
        super(policy, new Sarsa(policy, LEARNING_RATE, DISCOUNT_FACTOR));
    }

    private static QValueBasedPolicy createPolicy() {
        List<Action> possibleActions = new ArrayList<>(Move2DInt.getVonNeumannMove());

        return new QValueBasedPolicy(
                possibleActions,
                INITIAL_Q_VALUE,
                new EpsilonGreedyExponentialDecay(INITIAL_EPSILON, EPSILON_DECAY)
        );
    }
}
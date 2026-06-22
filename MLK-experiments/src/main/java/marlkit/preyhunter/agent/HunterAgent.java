package marlkit.preyhunter.agent;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Move2DDouble;
import learning.Algorithm;
import learning.Policy;
import learning.algorithms.Sarsa;
import learning.explorationstrategies.EpsilonGreedyExponentialDecay;
import learning.policies.QValueBasedPolicy;

/**
 * Hunter agent for the PreyHunter environment.
 */
public class HunterAgent extends AgentStandard {

    private final List<Action> possibleActions;

    /**
     * Creates a hunter agent with default policy and learning algorithm.
     */
    public HunterAgent() {
        super();

        this.possibleActions = new ArrayList<>(Move2DDouble.getVonNeumannMove());

        QValueBasedPolicy policy = new QValueBasedPolicy(
                possibleActions,
                1.0,
                new EpsilonGreedyExponentialDecay(1.0, 0.001)
        );

        Sarsa algorithm = new Sarsa(policy, 0.2, 0.95);

        setPolicy(policy);
        setAlgorithm(algorithm);
    }

    /**
     * Creates a hunter agent with custom policy and algorithm.
     *
     * @param policy agent policy
     * @param algorithm learning algorithm
     */
    public HunterAgent(Policy policy, Algorithm algorithm) {
        super(policy, algorithm);
        this.possibleActions = new ArrayList<>(Move2DDouble.getVonNeumannMove());
    }

    public List<Action> getPossibleActions() {
        return possibleActions;
    }
}
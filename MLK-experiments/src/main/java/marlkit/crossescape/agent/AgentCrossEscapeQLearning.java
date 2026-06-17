package marlkit.crossescape.agent;

import java.util.ArrayList;
import java.util.List;

import agent.action.Action;
import learning.algorithms.QLearning;
import learning.explorationstrategies.EpsilonGreedyExponentialDecay;
import learning.policies.QValueBasedPolicy;

/**
 * Q-learning implementation of a CrossEscape agent.
 */
public class AgentCrossEscapeQLearning extends AgentCrossEscape {

	public AgentCrossEscapeQLearning() {
		super();
		List<Action> possibleActions = new ArrayList<>(List.of(GO_LEFT, GO_RIGHT, GO_UP, GO_DOWN));
		QValueBasedPolicy qPolicy = new QValueBasedPolicy(possibleActions, 1.0,
				new EpsilonGreedyExponentialDecay(1.0, 0.001));
		QLearning qLearning = new QLearning(qPolicy, possibleActions, 0.2, 0.95);
		setPolicy(qPolicy);
		setAlgorithm(qLearning);
	}
}

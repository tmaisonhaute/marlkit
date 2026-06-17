package marlkit.maze;

import java.util.ArrayList;
import java.util.List;

import agent.action.Action;
import learning.algorithms.QLearning;
import learning.explorationstrategies.EpsilonGreedyExponentialDecay;
import learning.policies.QValueBasedPolicy;

public class AgentMazeEscapeQLearning extends AgentMazeEscape {

	public AgentMazeEscapeQLearning() {
		super();
		List<Action> possibleActions = new ArrayList<>(List.of(GO_LEFT, GO_RIGHT, GO_UP, GO_DOWN));
		QValueBasedPolicy policy = new QValueBasedPolicy(possibleActions, 1.0,
				new EpsilonGreedyExponentialDecay(1.0, 0.001));
		QLearning algorithm = new QLearning(policy, possibleActions, 0.2, 0.95);
		setPolicy(policy);
		setAlgorithm(algorithm);
	}
}

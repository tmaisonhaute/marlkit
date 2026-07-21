package marlkit.foraging.agent;

import java.util.ArrayList;
import java.util.List;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Move2DInt;
import learning.algorithms.QLearning;
import learning.explorationstrategies.EpsilonGreedyPowerDecay;
import learning.policies.QValueBasedPolicy;

/**
 * Independent Q-learning agent used in the Foraging experiment.
 */
public class ForagingAgent extends AgentStandard {

	public ForagingAgent() {
		super();

		List<Action> possibleActions = new ArrayList<>(
				List.of(Move2DInt.left(), Move2DInt.right(), Move2DInt.up(), Move2DInt.down())
		);

		QValueBasedPolicy policy = new QValueBasedPolicy(possibleActions, 1.0, new EpsilonGreedyPowerDecay(0.2));

		QLearning algorithm = new QLearning(policy, possibleActions, 0.2, 0.995);

		setPolicy(policy);
		setAlgorithm(algorithm);
	}
}
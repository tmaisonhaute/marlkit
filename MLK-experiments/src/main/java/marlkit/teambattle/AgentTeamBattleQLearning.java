package marlkit.teambattle;

import java.util.List;

import agent.action.Action;
import learning.algorithms.QLearning;
import learning.explorationstrategies.EpsilonGreedyExponentialDecay;
import learning.policies.QValueBasedPolicy;

/**
 * Q-learning implementation of a TeamBattle agent.
 */
public class AgentTeamBattleQLearning extends AgentTeamBattle {

	public AgentTeamBattleQLearning() {
		super();
		List<Action> possibleActions = defaultActions();
		QValueBasedPolicy qPolicy = new QValueBasedPolicy(possibleActions, 1.0,
				new EpsilonGreedyExponentialDecay(1.0, 0.001));
		QLearning qLearning = new QLearning(qPolicy, possibleActions, 0.2, 0.95);
		setPolicy(qPolicy);
		setAlgorithm(qLearning);
	}
}

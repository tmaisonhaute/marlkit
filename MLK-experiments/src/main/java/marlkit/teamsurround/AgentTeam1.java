package marlkit.teamsurround;

import java.util.List;

import agent.action.Action;
import learning.algorithm.QLearning;
import learning.policy.QValueBasedPolicy;
import learning.policy.explorationsettings.EpsilonGreedyExponentialDecay;

/**
 * Q-learning implementation for agents of team 1 in TeamSurround.
 */
public class AgentTeam1 extends AgentTeam {

	public AgentTeam1() {
		super();
		List<Action> actions = defaultActions();
		QValueBasedPolicy qPolicy = new QValueBasedPolicy(actions, 1.0,
				new EpsilonGreedyExponentialDecay(1.0, 0.001));
		QLearning qLearning = new QLearning(qPolicy, actions, 0.2, 0.95);
		setPolicy(qPolicy);
		setAlgorithm(qLearning);
	}
}
